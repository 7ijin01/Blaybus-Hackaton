package com.blaybus.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BankTransferRequestDTO {  // 계좌이체 결제 요청 DTO

    // 클라이언트가 결제 요청을 보낼 때 사용할 DTO
    private String reservationId;  // 예약 ID
    private String userId;  // 사용자 ID
    private String designerId;  // 디자이너 ID
    private double amount;  // 결제 금액

    private String accountNumber;  // 계좌번호
    private String depositorName;  // 입금자명
    private String bankName;  // 은행명
    private LocalDateTime depositTime;  // 입금 시간
}