package com.plus.service.user.error;

import com.plus.service.global.error.BaseExceptionCode;

public class TokenException extends RuntimeException implements BaseExceptionCode {
    private final int statusCode;
    private final String message;

    public TokenException() {
        super("Unauthorized: Token Error");
        this.statusCode = 401;
        this.message = "Unauthorized";
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
