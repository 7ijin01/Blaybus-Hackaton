package com.blaybus.entity;

import com.blaybus.entity.enums.PaymentMethod;
import com.blaybus.entity.enums.PaymentStatus;
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
    private String reservationId;
    private String userId;
    private String designerId;
    private PaymentStatus status;  // PENDING, SUCCESS, FAILED
    private double amount;  // 결제 금액
    private LocalDateTime createdAt;  // 결제 생성 시간
    private LocalDateTime updatedAt;  // 결제 상태 업데이트 시간

    // 계좌이체 결제에 필요한 필드
    private String accountNumber;  // 지정된 계좌번호
    private String depositorName;  // 입금자명
    private String bankName;  // 입금 계좌 은행 이름
    private LocalDateTime depositTime;  // 예금 시간

    private PaymentMethod paymentMethod;  // 결제 방식 (카카오페이 / 계좌이체)
}
