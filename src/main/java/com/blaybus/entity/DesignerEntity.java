package com.blaybus.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "designer")
public class DesignerEntity {
    @Id
    private String id;

    private String name;
    private String region;
    private String address;
    private String priceMeet;
    private String priceNotMeet;
    private String profile;
    private String field;  // JSON 형태로 저장
    private String introduction;
    private int meet;  // 0: 비대면, 1: 대면, 2: 둘 다
}
