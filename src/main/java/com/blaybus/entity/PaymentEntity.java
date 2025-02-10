package com.blaybus.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "payments")
public class PaymentEntity {
    @Id
    private String id;
    private String reservationId;
    private String userId;
    private String designerId;
    private String status;  // PENDING, SUCCESS, FAILED
    private double amount;
    private LocalDateTime createdAt;
}
