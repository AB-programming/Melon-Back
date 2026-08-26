package com.melon.commonservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.commonservice.common.HttpResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalControllerTest {

    private GlobalController globalController;

    @BeforeEach
    void setUp() {
        globalController = new GlobalController();
        ReflectionTestUtils.setField(globalController, "objectMapper", new ObjectMapper());
    }

    @Test
    void supports_returnsTrueForNonSpringdoc() throws Exception {
        MethodParameter returnType = new MethodParameter(String.class.getMethod("toString"), -1);
        assertThat(globalController.supports(returnType, null)).isTrue();
    }

    @Test
    void beforeBodyWrite_whenStringBody_serializesToJsonString() {
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        when(response.getHeaders()).thenReturn(new HttpHeaders());
        Object body = globalController.beforeBodyWrite("hello", null, null, null, null, response);
        assertThat(body).isInstanceOf(String.class);
        assertThat((String) body).contains("\"code\":200").contains("\"data\":\"hello\"");
    }

    @Test
    void beforeBodyWrite_whenHttpResultBody_returnsSameInstance() {
        HttpResult<Object> result = HttpResult.builder().code(200).message("OK").data("x").build();
        Object body = globalController.beforeBodyWrite(result, null, null, null, null, null);
        assertThat(body).isSameAs(result);
    }

    @Test
    void beforeBodyWrite_whenPlainObjectBody_wrapsIntoHttpResult() {
        Map<String, Object> data = Map.of("a", 1);
        Object body = globalController.beforeBodyWrite(data, null, null, null, null, null);
        assertThat(body).isInstanceOf(HttpResult.class);
        HttpResult<?> result = (HttpResult<?>) body;
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData()).isEqualTo(data);
    }
}
