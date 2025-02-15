package com.blaybus.domain.reservation.exception;


public class CustomException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public CustomException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public int getStatusCode() {
        return exceptionCode.getStatusCode();
    }
}