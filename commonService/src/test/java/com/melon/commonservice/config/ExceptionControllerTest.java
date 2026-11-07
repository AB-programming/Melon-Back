package com.melon.commonservice.config;

import com.melon.commonservice.common.HttpResponseStatus;
import com.melon.commonservice.common.HttpResult;
import com.melon.commonservice.exception.ServerException;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionControllerTest {

    private final ExceptionController exceptionController = new ExceptionController();

    @Test
    void globalException_returnsInternalServerError() {
        HttpResult<String> result = exceptionController.globalException(new RuntimeException("boom"));
        assertThat(result.getCode()).isEqualTo(HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode());
        assertThat(result.getData()).isEqualTo("INTERNAL_SERVER_ERROR");
    }

    @Test
    void serverException_returnsMessage() {
        HttpResult<String> result = exceptionController.serverException(new ServerException("boom"));
        assertThat(result.getCode()).isEqualTo(HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode());
        assertThat(result.getData()).isEqualTo("boom");
    }

    @Test
    void accessDeniedException_returnsForbidden() {
        HttpResult<String> result = exceptionController.accessDeniedException(new AccessDeniedException("denied"));
        assertThat(result.getCode()).isEqualTo(HttpResponseStatus.FORBIDDEN.getCode());
        assertThat(result.getData()).isEqualTo("Forbidden");
    }

    @Test
    void authorizationDeniedException_returnsUnauthorized() {
        HttpResult<String> result = exceptionController.authorizationDeniedException(
                new AuthorizationDeniedException("denied", () -> false));
        assertThat(result.getCode()).isEqualTo(HttpResponseStatus.UNAUTHORIZED.getCode());
        assertThat(result.getData()).isEqualTo("denied");
    }

    @Test
    void missingServletRequestParameterException_returnsBadRequest() {
        HttpResult<String> result = exceptionController
                .missingServletRequestParameterException(new MissingServletRequestParameterException("userId", "String"));
        assertThat(result.getCode()).isEqualTo(HttpResponseStatus.BAD_REQUEST.getCode());
        assertThat(result.getData()).isEqualTo("The userId cannot be empty!");
    }
}
