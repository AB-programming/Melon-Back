package com.melon.commonservice.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpResult<T> {
    private Integer code;
    private String message;
    private T data;
}
