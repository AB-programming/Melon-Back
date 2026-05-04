package com.melon.commonservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.commonservice.common.HttpResponseStatus;
import com.melon.commonservice.common.HttpResult;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalController implements ResponseBodyAdvice<Object> {
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.getDeclaringClass().getName().contains("springdoc");
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpResult<Object> result = HttpResult.builder()
                .code(HttpResponseStatus.OK.getCode())
                .message(HttpResponseStatus.OK.getMessage())
                .data(body)
                .build();
        if (body instanceof String) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return objectMapper.writeValueAsString(result);
        }
        if (body instanceof HttpResult) {
            return body;
        }
        return result;
    }
}
