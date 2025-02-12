package com.blaybus.repository;

import com.blaybus.entity.ReservationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReserverationRepository extends MongoRepository<ReservationEntity, String> {

    // 예약 ID로 예약 내역 조회
    ReservationEntity findByUserIdAndDesignerId(String userId, String designerId);

    // 사용자 ID로 예약 내역 조회
    List<ReservationEntity> findByUserId(String userId);
}
