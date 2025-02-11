package com.blaybus.reservation.entity;

import lombok.*;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private Map<String, List<Map<String, String>>> timeTable;

    public void setTimeTable(String date, Map<String, String> newReservation) {
        // 날짜별 예약 리스트를 가져오거나, 없으면 새 리스트 생성
        timeTable.computeIfAbsent(date, k -> new ArrayList<>()).add(newReservation);
    }
}
