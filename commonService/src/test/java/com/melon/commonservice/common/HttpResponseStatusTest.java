package com.melon.commonservice.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseStatusTest {

    @Test
    void values_areCorrect() {
        assertThat(HttpResponseStatus.OK.getCode()).isEqualTo(200);
        assertThat(HttpResponseStatus.OK.getMessage()).isEqualTo("OK");
        assertThat(HttpResponseStatus.BAD_REQUEST.getCode()).isEqualTo(400);
        assertThat(HttpResponseStatus.UNAUTHORIZED.getCode()).isEqualTo(401);
        assertThat(HttpResponseStatus.FORBIDDEN.getCode()).isEqualTo(403);
        assertThat(HttpResponseStatus.NOT_FOUND.getCode()).isEqualTo(404);
        assertThat(HttpResponseStatus.CONFLICT.getCode()).isEqualTo(409);
        assertThat(HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode()).isEqualTo(500);
    }
}
