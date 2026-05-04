package com.melon.commonservice.exception;

import lombok.Getter;

@Getter
public class ServerException extends Exception{
    private String message;

    public ServerException() {
        super();
    }

    public ServerException(String message) {
        super(message);
        this.message = message;
    }
}
