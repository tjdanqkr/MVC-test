package com.plus.service.user.error;

import com.plus.service.global.error.BaseExceptionCode;

public enum UserErrorCode implements BaseExceptionCode {
    LOGIN_FAILED(401, "Login failed"),
    USER_NOT_FOUND(404, "User not found"),
    USER_ALREADY_EXISTS(409, "User already exists");

    private final int statusCode;
    private final String message;

    UserErrorCode(int statusCode, String message) {
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
