package com.blaybus.glowup_backend.controller;

import com.blaybus.glowup_backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        log.info("readList={}", userId);
        Map<String, Object> param = new HashMap<>();
        param.put("userId",userId);
        log.info("param={}", param);

        List<String> reservationList = reservationService.read(param);
        log.info("reservationList={}", reservationList);
        return ResponseEntity.ok(reservationList);
    }
}