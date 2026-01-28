package com.lichbalab.docs.api.filter;

import com.lichbalab.docs.api.error.LLabErrorCode;
import com.lichbalab.docs.api.error.LLabException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Simple in-memory, per-user rate-limiting filter that allows
 * at most 5 validation requests for the entire lifetime of the application.
 *
 * Endpoints starting with /docs/validate/ are counted.
 * If the quota is exceeded the filter returns HTTP 429.
 *
 * NOTE: Counts are stored in memory and will reset after application restart.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 5;

    private final Map<String, Integer> userCounters = new ConcurrentHashMap<>();

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (!path.startsWith("/docs/validate/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId = resolveUserId();
        // anonymous users should not reach these endpoints because they are authenticated()
        // but just in case fall back to remote address
        if (userId == null || userId.isBlank()) {
            userId = "anon:" + request.getRemoteAddr();
        }

        int used = userCounters.getOrDefault(userId, 0);
        if (used >= MAX_REQUESTS) {
            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    new LLabException(LLabErrorCode.TOO_MANY_VALIDATION_REQUESTS,
                            new Object[]{MAX_REQUESTS})
            );
            return; // stop further processing
        }

        userCounters.put(userId, used + 1);
        filterChain.doFilter(request, response);
    }

    /**
     * Extract a stable user identifier from the Spring Security context.
     * For JWT authentication the subject claim is exposed as principal name.
     */
    private String resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
