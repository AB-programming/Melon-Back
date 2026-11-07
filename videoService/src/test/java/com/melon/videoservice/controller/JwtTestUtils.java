package com.melon.videoservice.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;

public final class JwtTestUtils {

    private JwtTestUtils() {
    }

    public static void setAuthenticatedUser(String userId) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .claim("user", Map.of("id", userId))
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    public static void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }
}
