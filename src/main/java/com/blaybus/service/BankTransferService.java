package com.blaybus.service;

import com.blaybus.dto.BankTransferRequestDTO;
import com.blaybus.entity.BankTransferInfo;
import com.blaybus.entity.PaymentEntity;
import com.blaybus.entity.enums.PaymentMethod;
import com.blaybus.entity.enums.PaymentStatus;
import com.blaybus.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BankTransferService {

    private final PaymentRepository paymentRepository;

    // 계좌이체 결제 처리
    public String processBankTransfer(BankTransferRequestDTO requestDTO) {
        // 계좌이체 정보 생성
        BankTransferInfo bankTransferInfo = BankTransferInfo.builder()
                .accountNumber(requestDTO.getAccountNumber())  // 고객이 입금할 계좌번호
                .bankName(requestDTO.getBankName())  // 은행명
                .depositorName(requestDTO.getDepositorName())  // 예금자명
                .build();

        // 결제 정보 생성
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .reservationId(requestDTO.getReservationId())
                .userId(requestDTO.getUserId())
                .designerId(requestDTO.getDesignerId())
                .amount(requestDTO.getAmount())
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .status(PaymentStatus.PENDING)  // 입금 대기 상태
                .createdAt(LocalDateTime.now())
                .bankTransferInfo(bankTransferInfo)  // 계좌이체 정보 연결
                .build();

        // 결제 정보 DB에 저장
        paymentEntity = paymentRepository.save(paymentEntity);

        return paymentEntity.getId();
    }

    // 입금 확인 처리
    public void confirmDeposit(String paymentId) {
        // 결제 정보 조회
        PaymentEntity paymentEntity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // 입금 정보 업데이트
        paymentEntity.setStatus(PaymentStatus.SUCCESS);  // 입금 완료 상태
        paymentEntity.getBankTransferInfo().setDepositTime(LocalDateTime.now());  // 입금 시간 설정

        // 결제 정보 업데이트
        paymentRepository.save(paymentEntity);
    }
}
