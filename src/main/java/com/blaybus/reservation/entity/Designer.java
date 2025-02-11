package com.blaybus.reservation.entity;

import lombok.*;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(collection = "designer")

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Designer
{
    @Id
    private String id;
    private String name;
    private String region;
    private String address;
    private Integer price_meet;
    private Integer price_not_meet;
    private String profile;
    private String field;
    private String introduction;
    private Integer meet;
    private List<String> timeTable = new ArrayList<>();

}
