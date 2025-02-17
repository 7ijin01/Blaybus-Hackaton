package com.blaybus.domain.payment.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {

    PAY_CANCEL(400, "결제가 취소되었습니다."),
    PAY_FAILED(400, "결제에 실패했습니다."),
    PAY_NOT_FOUND(400,"결제 정보가 없습니다");

    private final int statusCode;
    private final String message;

    ExceptionCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
