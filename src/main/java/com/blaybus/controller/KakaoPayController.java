package com.blaybus.controller;

import com.blaybus.dto.KakaoPayReadyResponseDTO;
import com.blaybus.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    /**
     * 결제 요청
     */
    @PostMapping("/ready")
    public KakaoPayReadyResponseDTO readyToKakaoPay() {
        return kakaoPayService.kakaoPayReady();
    }
}
