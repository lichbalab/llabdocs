package com.lichbalab.docs.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes runtime configuration needed by the UI, such as Google OAuth client-id.
 * The value is read from the environment variable GOOGLE_CLIENT_ID
 * (fallback to empty string if not provided).
 */
@RestController
public class OAuthConfigController {

    @Value("${google.oauth.client-id:}")
    private String googleClientId;

    @GetMapping("/api/config/google-client-id")
    public ResponseEntity<String> googleClientId() {
        return ResponseEntity.ok(googleClientId);
    }
}
