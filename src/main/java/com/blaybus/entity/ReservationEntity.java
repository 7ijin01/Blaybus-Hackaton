package com.blaybus.entity;

import com.blaybus.entity.enums.MeetingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "reservation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationEntity {
    @Id
    private String id;

    private String userId;
    private String designerId;
    private MeetingType meet;
    private LocalDateTime date;
    private LocalDateTime start;
    private LocalDateTime end;
    private String shop;
    private double price;
}
