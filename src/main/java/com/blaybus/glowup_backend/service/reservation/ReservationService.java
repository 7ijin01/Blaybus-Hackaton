package com.blaybus.glowup_backend.service.reservation;

import com.blaybus.glowup_backend.model.Reservations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Reservations> findByUserId(String userId){
        log.info("findByUserId userId={}", userId);

        if (userId == null) {
            log.warn("read; userId is invalid");
            return new ArrayList<>();
        }
        List<Reservations> list = mongoTemplate.find(
                new Query(Criteria.where("userId").is(userId)), Reservations.class); // 기준 잡아서 시간 순으로 조회 되는 부분 추가하기
        log.info("list={}", list);
        return list;
    }
}
