package com.lichbalab.docs.error;

import java.util.Date;

import com.lichbalab.cmc.core.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class ErrorResponseBuilder {

    MessageSource messageSource;

    @Autowired
    public ErrorResponseBuilder(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public ErrorResponse buildErrorResponse(HttpServletRequest request, ErrorCode errorCode, Object[] errorParams) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getServletPath());

        String randomString = "#" + RandomStringGenerator.generateRandomString(10);
        Object[] params = ArrayUtils.add(errorParams, randomString);
        String message = messageSource.getMessage(errorCode.name(), params, request.getLocale());
        errorResponse.setMessage(message);

        errorResponse.setStatus(errorCode.getStatus());

        return errorResponse;
    }
}
