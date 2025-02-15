package com.blaybus.domain.reservation.service;

import com.blaybus.domain.reservation.repository.ReservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.time.Instant;

@Service
public class ReservationCleanupService {
    private final ReservationRepository reservationRepository;

    public ReservationCleanupService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Scheduled(fixedRate = 60000*60) // 1시간마다 실행 (60,000ms = 1분)
    public void cleanupExpiredReservations() {
        Date oneMinuteAgo = Date.from(Instant.now().minusSeconds(60*60));//예약 생성 후 결제를 안한 1시간 뒤 예약 삭제

        reservationRepository.deleteByCreatedAtBeforeAndStatusNot(oneMinuteAgo, "CONFIRMED");
    }
}

