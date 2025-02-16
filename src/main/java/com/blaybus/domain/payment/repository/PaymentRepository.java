package com.blaybus.domain.payment.repository;

import com.blaybus.domain.payment.entity.PaymentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<PaymentEntity, String> {

    // 사용자 ID로 결제 내역 조회
    List<PaymentEntity> findByUserId(String userId);

    // 특정 예약 ID로 결제 내역 조회
    PaymentEntity findByReservationId(String reservationId);

}
