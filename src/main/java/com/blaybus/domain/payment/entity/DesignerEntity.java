package com.blaybus.domain.payment.entity;

import com.blaybus.domain.payment.entity.enums.MeetingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "designer")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesignerEntity {
    @Id
    private String id;

    private String name;
    private String region;
    private String address;
    private double priceMeet;
    private double priceNotMeet;
    private String profile;
    private String field;  // JSON 형태로 저장
    private String introduction;
    private MeetingType meet;
}
