package com.blaybus.reservation.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Document(collection = "reservations")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

//TODO: mongoDB TTL 설정 후 일정시간동안 예약하지 않을 시 자동 삭제
//ex) 예약 신청 후 비대면, 대면만 선택하고 디자이너,날짜를 선택 안하고 앱 종료시
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


    private String status;// "PENDING", "CONFIRMED", "CANCELED"

}
