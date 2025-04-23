package com.plus.service.global.error;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException{
    private final int statusCode;
    private final String message;
    protected BusinessException(BaseExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.statusCode = exceptionCode.getStatusCode();
        this.message = exceptionCode.getMessage();
    }
}
