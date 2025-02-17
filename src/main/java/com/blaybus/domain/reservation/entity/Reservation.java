package com.blaybus.domain.reservation.entity;


import com.blaybus.domain.reservation.dto.ReservationRequestDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Slf4j
@Document(collection = "reservations")
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class Reservation
{
    @Id
    private String id;
    private String userId;
    private String designerId;
    private Boolean meet;
    private LocalDate date;
    private LocalTime start;
    private LocalTime end;
    private String shop;
    private String price;
    private String googleMeetUri;
    private String status;// "PENDING", "CONFIRMED", "CANCELED"
    private String method;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date createdAt;




    @Builder
    public static Reservation buildReservation(
            ReservationRequestDto dto
    ) {

        log.info("Date: {}", dto.getDate());
        log.info("start: {}", dto.getStart());
        log.info("End: {}", dto.getEnd());
        return Reservation.builder()
                .designerId(dto.getDesignerId())
                .meet(dto.getMeet())
                .date(dto.getDate())
                .start(dto.getStart())
                .end(dto.getEnd())
                .shop(dto.getShop())
                .price(dto.getPrice())
                .build();
    }



}
