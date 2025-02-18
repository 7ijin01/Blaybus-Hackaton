package com.blaybus.domain.reservation.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDto {
    private String designerId;
    private Boolean meet;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date; // 날짜는 문자열로 받음

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime start; // start를 문자열("HH:mm:ss")로 변환

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime end;
    private String shop;
    private String price;
    private String method;
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





