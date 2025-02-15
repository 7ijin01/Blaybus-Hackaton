package com.blaybus.glowup_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collation = "")
@AllArgsConstructor
public class Payment {
    private String reservationsId; // 예약번호
    private String userId; //사용자 아이디
    private String designerId; //디자이너 아이디
    private String status; //예약 상태
    private Double amount; // 금액
    private Date createdAt; // 생성일자
    private String accountNumber; // 계좌 번호
    private String depositorName; // 입금자 서명
    private String bankName; // 은행 이름
    private Date depositTime; // 입금 시간
    private String paymentMethod; // 결제 유형
    private List<kakaopay> kakaoPayInfo;
    private List<bankTransfer> bankTransferInfo;
}
