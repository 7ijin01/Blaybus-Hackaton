package com.blaybus.domain.reservation.entity;

import lombok.*;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

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
    private Map<String, Integer> price; // { "offline": 40000, "online": 20000 }
    private List<String> type; // ["대면", "비대면"]
    private String profile;
    private List<String> portfolios; // 사진
    private List<String> videos; // 상세 페이지에 띄울 영상
    private String field;
    private String introduction;


}
