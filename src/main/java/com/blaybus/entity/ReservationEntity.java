package com.blaybus.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "reservation")
public class ReservationEntity {
    @Id
    private String id;

    private String userId;
    private String designerId;
    private boolean meet;  // true: 대면, false: 비대면
    private LocalDateTime date;
    private LocalDateTime start;
    private LocalDateTime end;
    private String shop;
    private String price;
}
