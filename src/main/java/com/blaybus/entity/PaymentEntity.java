package com.blaybus.entity;

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
    private double amount;
    private LocalDateTime createdAt;
}
