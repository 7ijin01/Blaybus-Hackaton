package com.blaybus.domain.payment.controller;

import com.blaybus.domain.payment.dto.BankTransferRequestDTO;
import com.blaybus.domain.payment.service.BankTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "계좌이체 결제 API", description = "계좌이체 결제 관련 기능 제공")
@RestController
@RequestMapping("/payment/bank-transfer")
@RequiredArgsConstructor
public class BankTransferController {
    private final BankTransferService bankTransferService;

    /**
     * 계좌이체 결제 요청
     */
//    @Operation(summary = "계좌이체 결제 요청", description = "사용자가 계좌이체로 결제를 요청함")
//    @PostMapping("/request")
//    public ResponseEntity<String> requestBankTransfer(@RequestBody BankTransferRequestDTO requestDTO) {
//        String paymentId = bankTransferService.processBankTransfer(requestDTO);
//        return ResponseEntity.ok("결제 준비 완료. 입금 대기 중. paymentId: " + paymentId); // paymentId 반환
//    }

    /**
     * 계좌이체 입금 확인 처리
     */
    @Operation(summary = "입금 확인 처리", description = "사용자의 입금 여부를 확인하고 결제를 완료함. 결제 요청 후 반환되는 PaymentId 입력하기")
    @PostMapping("/confirm/{paymentId}")
    public ResponseEntity<String> confirmDeposit(@PathVariable String paymentId) {
        try {
            bankTransferService.confirmDeposit(paymentId);
            return ResponseEntity.ok("입금 확인 완료. 결제 완료");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("입금 확인 실패: " + e.getMessage());
        }
    }

}
