package com.blaybus.reservation.dto;

import com.blaybus.reservation.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponseDto
{
    private String reservationId;
    private String userId;
    private String designerId;
    private Boolean meet;
    private LocalDate date;
    private LocalTime start;
    private LocalTime end;
    private String shop;
    private String price;
    private String googleMeetUri;

    public ReservationResponseDto(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.userId = reservation.getUserId();
        this.designerId = reservation.getDesignerId();
        this.meet = reservation.getMeet();
        this.date = reservation.getDate();
        this.start = reservation.getStart();
        this.end = reservation.getEnd();
        this.shop = reservation.getShop();
        this.price = reservation.getPrice();
        this.googleMeetUri = reservation.getGoogleMeetUri();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ReservationTimeResponse {
        private String date;
        private List<String> availableTimes;
    }

//    @Getter
//    @Setter
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class ReservationResponse {
//        private String reservationId;
//        private String status;
//
//
//
//        public ReservationResponse(Reservation reservation)
//        {
//            this.reservationId=reservation.getId();
//            this.status=reservation.getStatus();
//        }
//    }
//
//    @Getter
//    @Setter
//    @AllArgsConstructor
//    public static class ReservationMeetResponse {
//        private String reservationId;
//        private boolean meet;
//    }
//
//    @Getter
//    @Setter
//    @AllArgsConstructor
//    public static class ReservationDesignerResponse {
//        private String reservationId;
//        private String designerId;
//        private String shop;
//        private String price;
//    }

}
