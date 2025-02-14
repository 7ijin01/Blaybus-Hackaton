package com.blaybus.exception;

public class BusinessLogicException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public BusinessLogicException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public int getStatusCode() {
        return exceptionCode.getStatusCode();
    }
}
