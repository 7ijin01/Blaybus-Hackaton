package com.blaybus.domain.register.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user") // MongoDB 컬렉션 이름 지정
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id; // MongoDB의 기본 키 (ObjectId)

    private String googleId; // Google OAuth ID
    private String name; // 사용자 이름
    private String gender; // 성별 ("M", "F" 또는 기타)

}