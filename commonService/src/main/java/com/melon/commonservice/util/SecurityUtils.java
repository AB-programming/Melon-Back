package com.melon.commonservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.commonservice.pojo.vo.UserVo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class SecurityUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private SecurityUtils() {
    }

    public static Jwt getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication)
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .map(JwtAuthenticationToken::getToken)
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
    }

    public static String getCurrentUserId() {
        return getCurrentUserVo().getId();
    }

    public static String getCurrentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Map<String, Object> userClaim = jwtAuth.getToken().getClaimAsMap("user");
            if (Objects.nonNull(userClaim) && Objects.nonNull(userClaim.get("id"))) {
                return userClaim.get("id").toString();
            }
        }
        return null;
    }

    public static UserVo getCurrentUserVo() {
        Map<String, Object> userClaim = getJwt().getClaimAsMap("user");
        if (userClaim == null) {
            throw new IllegalStateException("The user claim is missing in the token");
        }
        return objectMapper.convertValue(userClaim, UserVo.class);
    }
}
