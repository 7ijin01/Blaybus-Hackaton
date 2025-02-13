package com.blaybus.glowup_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document(collation = "")
@AllArgsConstructor
public class Designers {
    private String name;
    private String region;
    private String address;
    private int price_meet;
    private int price_not_meet;
    private String profile;
    private String introduction;
    private int meet;
    private List<String> timeTable;
    private String designerId;
}
