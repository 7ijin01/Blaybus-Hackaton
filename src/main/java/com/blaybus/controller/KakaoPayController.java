package com.blaybus.controller;

import com.blaybus.dto.KakaoApproveRequestDTO;
import com.blaybus.dto.KakaoApproveResponseDTO;
import com.blaybus.dto.KakaoPayReadyResponseDTO;
import com.blaybus.dto.KakaoPayRequestDTO;
import com.blaybus.exception.BusinessLogicException;
import com.blaybus.exception.ExceptionCode;
import com.blaybus.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment/kakao")
@RequiredArgsConstructor
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    /**
     * 결제 요청
     */
    @PostMapping("/ready")
    public KakaoPayReadyResponseDTO readyToKakaoPay(@RequestBody KakaoPayRequestDTO kakaoPayRequestDTO) {
        return kakaoPayService.kakaoPayReady(kakaoPayRequestDTO);
    }

    /**
     * 결제 성공
     */
    @PostMapping("/success")
    public ResponseEntity<KakaoApproveResponseDTO> afterPayRequest(@RequestBody KakaoApproveRequestDTO requestDTO) {
        KakaoApproveResponseDTO kakaoApprove = kakaoPayService.approveResponse(requestDTO);

        return new ResponseEntity<>(kakaoApprove, HttpStatus.OK);
    }

    /**
     * 결제 진행 중 취소
     */
    @GetMapping("/cancel")
    public void cancel() {
        throw new BusinessLogicException(ExceptionCode.PAY_CANCEL);
    }

    /**
     * 결제 실패
     */
    @GetMapping("fail")
    public void fail() {
        throw new BusinessLogicException(ExceptionCode.PAY_FAILED);
    }

    /**
     * 예외 처리: BusinessLogicException 처리
     */
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<String> handleBusinessLogicException(BusinessLogicException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.valueOf(ex.getStatusCode()));
    }
}
