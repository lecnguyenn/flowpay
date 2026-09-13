package com.flowpay.common.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class AppException extends RuntimeException{

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
