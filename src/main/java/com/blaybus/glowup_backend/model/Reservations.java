package com.blaybus.glowup_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collation = "") // MongoDB 컬렉션 이름
@AllArgsConstructor
public class Reservations {
    private String userId; //사용자 아이디
    private String status; //예약 상태
    private Boolean meet; //미팅 상태
    private String designerId; //디자이너 아이디
    private Date date; // 날짜
    private Date start; // 시작 날짜
    private Date end; // 종료 날짜
    private String shop; // 샵 위치
    private String price; // 가격
    private Date expireAt; //만료 날짜?
    private String reservationsId; // 예약번호
}
