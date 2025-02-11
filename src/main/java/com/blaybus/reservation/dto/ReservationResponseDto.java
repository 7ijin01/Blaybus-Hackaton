package com.blaybus.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
}
