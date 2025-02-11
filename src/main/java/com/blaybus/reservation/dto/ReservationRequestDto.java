package com.blaybus.reservation.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;


public class ReservationRequestDto
{
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReservationDesignerIdRequest
    {
        private String designerId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReservationMeetRequest
    {
        private boolean meet;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReservationDateRequest
    {
        private LocalDate date;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReservationDateAndTimeRequest
    {
        private LocalDate date;
        private LocalTime time;
    }


}
