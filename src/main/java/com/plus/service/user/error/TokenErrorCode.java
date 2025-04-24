package com.plus.service.user.error;

import com.plus.service.global.error.BaseExceptionCode;

public enum TokenErrorCode implements BaseExceptionCode {
    USER_TOKEN_EXPIRED(403, "User token expired"),
    USER_TOKEN_INVALID(403, "User token invalid");
    private final int statusCode;
    private final String message;
    TokenErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
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
