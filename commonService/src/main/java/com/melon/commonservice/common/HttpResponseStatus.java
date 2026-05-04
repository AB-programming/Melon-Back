package com.melon.commonservice.common;

import lombok.Getter;

@Getter
public enum HttpResponseStatus {
    OK(200, "OK"),
    BAD_REQUEST(400, "BAD_REQUEST"),

    UNAUTHORIZED(401, "UNAUTHORIZED"),

    FORBIDDEN(403, "FORBIDDEN"),

    NOT_FOUND(404, "NOT_FOUND"),

    CONFLICT(409, "CONFLICT"),

    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");

    private final int code;
    private final String message;

    HttpResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

}