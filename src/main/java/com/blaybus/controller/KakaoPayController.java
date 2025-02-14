package com.blaybus.controller;

import com.blaybus.dto.KakaoApproveRequestDTO;
import com.blaybus.dto.KakaoApproveResponseDTO;
import com.blaybus.dto.KakaoPayReadyResponseDTO;
import com.blaybus.dto.KakaoPayRequestDTO;
import com.blaybus.exception.BusinessLogicException;
import com.blaybus.exception.ExceptionCode;
import com.blaybus.service.KakaoPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "카카오페이 결제 API", description = "카카오페이 결제 관련 기능 제공")
@RestController
@RequestMapping("/payment/kakao")
@RequiredArgsConstructor
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    /**
     * 결제 요청
     */
    @Operation(summary = "카카오페이 결제 요청", description = "사용자가 카카오페이로 결제를 요청함. 테스트 할려면 application-pay.yml에 본인 카카오페이 secretKey(Dev) 발급 받아서 넣어야 함. 반환된 url(app, mobile, pc) 중 링크 복붙 해주면 카카오페이 결제 창 뜸. 결제 처리 한 다음에 승인으로 넘어가기")
    @PostMapping("/ready")
    public KakaoPayReadyResponseDTO readyToKakaoPay(@RequestBody KakaoPayRequestDTO kakaoPayRequestDTO) {
        return kakaoPayService.kakaoPayReady(kakaoPayRequestDTO);
    }

    /**
     * 결제 성공
     */
    @Operation(summary = "카카오페이 결제 승인", description = "결제 요청 후, 승인 과정 처리함")
    @PostMapping("/success")
    public ResponseEntity<KakaoApproveResponseDTO> afterPayRequest(@RequestBody KakaoApproveRequestDTO requestDTO) {
        KakaoApproveResponseDTO kakaoApprove = kakaoPayService.approveResponse(requestDTO);

        return new ResponseEntity<>(kakaoApprove, HttpStatus.OK);
    }

    /**
     * 결제 진행 중 취소
     */
    @Operation(summary = "카카오페이 결제 진행 중 취소", description = "사용자가 결제 진행 중 취소를 선택한 경우 예외를 발생시킴")
    @GetMapping("/cancel")
    public void cancel() {
        throw new BusinessLogicException(ExceptionCode.PAY_CANCEL);
    }

    /**
     * 결제 실패
     */
    @Operation(summary = "카카오페이 결제 실페", description = "카카오페이 결제가 실패한 경우 예외를 발생시킴")
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
