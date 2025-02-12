package com.blaybus.controller;

import com.blaybus.dto.BankTransferRequestDTO;
import com.blaybus.service.BankTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment/bank-transfer")
@RequiredArgsConstructor
public class BankTransferController {
    private final BankTransferService bankTransferService;

    // 계좌이체 결제 요청
    @PostMapping("/request")
    public ResponseEntity<String> requestBankTransfer(@RequestBody BankTransferRequestDTO requestDTO) {
        bankTransferService.processBankTransfer(requestDTO);
        return ResponseEntity.ok("입금 확인 중");
    }

    // 입금 확인 처리
    @PostMapping("/confirm/{paymentId}")
    public ResponseEntity<String> confirmDeposit(@PathVariable String paymentId) {
        bankTransferService.confirmDeposit(paymentId);
        return ResponseEntity.ok("입금 확인 완료");
    }

}
