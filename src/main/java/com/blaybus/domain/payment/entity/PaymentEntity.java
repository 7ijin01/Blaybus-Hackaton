package com.blaybus.domain.payment.entity;

import com.blaybus.domain.payment.dto.AmountDTO;
import com.blaybus.domain.payment.entity.enums.PaymentMethod;
import com.blaybus.domain.payment.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity {
    @Id
    private String id;
    private String reservationId;  // 예약 ID
    private String userId;
    private String designerId;
    private AmountDTO amount;  // 결제 금액
    private PaymentMethod paymentMethod;  // 결제 방식 (카카오페이 / 계좌이체)
    private PaymentStatus status;  // 결제 상태 (PENDING, SUCCESS, FAILED, CANCELLED)
    private LocalDateTime createdAt;  // 결제 요청 시간

    private KakaoPayInfo kakaoPayInfo;  // 카카오페이 결제 정보
    private BankTransferInfo bankTransferInfo;  // 계좌이체 결제 정보
}
