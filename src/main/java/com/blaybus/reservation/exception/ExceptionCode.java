package com.blaybus.reservation.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode
{
    INVALID_REQUEST(400, "잘못된 요청입니다."),
    DESIGNER_NOT_FOUND(404, "해당 디자이너를 찾을 수 없습니다."),
    RESERVATION_NOT_FOUND(404, "해당 예약을 찾을 수 없습니다."),
    GOOGLE_CALENDAR_ERROR(500, "Google Calendar API 호출 중 오류가 발생했습니다.");

    private final int statusCode;
    private final String message;

    ExceptionCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
