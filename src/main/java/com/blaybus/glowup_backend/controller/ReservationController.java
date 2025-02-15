package com.blaybus.glowup_backend.controller;

import com.blaybus.glowup_backend.model.Reservations;
import com.blaybus.glowup_backend.service.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * 회원의 예약 정보 조회
 * @since : 2025 02-12
 * @author : yongcrane96
 */

@RestController
@RequestMapping("/reservation")
@Slf4j
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping("")
    public ResponseEntity<?> readList(@RequestParam String userId) {
        log.info("readList userId={}", userId);
        List<Reservations> reservationList = reservationService.findByUserId(userId);
        log.info("reservationList={}", reservationList);
        return ResponseEntity.ok(reservationList);
    }

    @DeleteMapping("")
    public ResponseEntity<?> delete(@RequestParam String reservationsId, String designerId) {
        log.info("delete reservationsId={}, designerId={}", reservationsId, designerId);
        boolean result = reservationService.delete(reservationsId, designerId);
        log.info("result={}", result);
        return ResponseEntity.ok(result);
    }

    @PostMapping("")
    public ResponseEntity<?> update(@RequestParam String reservationsId, String designerId) {
        log.info("update reservationsId={}, designerId={}", reservationsId, designerId);
        int result = reservationService.update(reservationsId, designerId);
        log.info("result={}", result);
        return ResponseEntity.ok(result);
    }
}