package com.blaybus.reservation.entity;

import lombok.*;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "designers")

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


}
