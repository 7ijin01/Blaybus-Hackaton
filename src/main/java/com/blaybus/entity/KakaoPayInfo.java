package com.blaybus.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoPayInfo {
    private String tid;  // 결제 고유 번호
    private String aid;  // 요청 고유 번호
    private String cid;  // 가맹점 코드
    private String sid;  // 정기 결제용 ID (정기결제 시)
    private String partnerOrderId;  // 가맹점 주문 번호
    private String partnerUserId;  // 가맹점 회원 ID
    private String paymentMethodType;  // 결제 수단 (카드, 계좌이체 등)

    private String itemName;  // 상품명
    private int quantity;  // 상품 수량
    private double totalAmount;  // 총 금액
    private double vatAmount;  // 부가세
    private double taxFreeAmount;  // 비과세 금액

    private LocalDateTime createdAt;  // 결제 요청 시간
    private LocalDateTime approvedAt;  // 결제 승인 시간
}
