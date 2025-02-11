package com.blaybus.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nonapi.io.github.classgraph.json.Id;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationResponseDto
{
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReservationResponse {
        private String reservationId;
        private String status;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReservationMeetResponse {
        private String reservationId;
        private boolean meet;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReservationDesignerResponse {
        private String reservationId;
        private String designerId;
        private String shop;
        private String price;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReservationTimeResponse {
        private String date;
        private List<String> availableTimes;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserReservationResponse {
        private String userId;
        private String designerId;
        private Boolean meet;
        private LocalDate date;
        private LocalTime start;
        private LocalTime end;
        private String shop;
        private String price;
        private String status;// "PENDING", "CONFIRMED", "CANCELED"
    }
}
