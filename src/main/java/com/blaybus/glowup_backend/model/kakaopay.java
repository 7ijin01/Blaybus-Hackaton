package com.blaybus.glowup_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class kakaopay {
    private String tid;
    private String aid;
    private String cid;
    private String partnerOrderId;
    private String partnerUserId;
    private String paymentMethodType;
    private String itemName;
    private int quantity;
    private int totalAmount;
    private double vatAmount;
    private double taxFreeAmount;
    private String createdAt;
    private String approvedAt;
}
