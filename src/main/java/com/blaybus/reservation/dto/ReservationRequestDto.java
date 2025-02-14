package com.blaybus.reservation.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDto {
    private String designerId;
    private Boolean meet;
    private LocalDate date;
    private LocalTime start;
    private LocalTime end;
    private String shop;
    private String price;

}











//    @Getter
//    @Setter
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class ReservationDesignerIdRequest
//    {
//        private String designerId;
//        private boolean meet;
//    }
//
//    @Getter
//    @Setter
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class ReservationMeetRequest
//    {
//        private boolean meet;
//    }
//
//    @Getter
//    @Setter
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class ReservationDateRequest
//    {
//        private LocalDate date;
//    }
//    @Getter
//    @Setter
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class ReservationDateAndTimeRequest
//    {
//        private LocalDate date;
//        private LocalTime time;
//    }





