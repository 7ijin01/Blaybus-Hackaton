package com.blaybus.service;

import com.blaybus.dto.BankTransferRequestDTO;
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
    public void processBankTransfer(BankTransferRequestDTO requestDTO) {
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .reservationId(requestDTO.getReservationId())
                .userId(requestDTO.getUserId())
                .designerId(requestDTO.getDesignerId())
                .amount(requestDTO.getAmount())
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        paymentEntity.setAccountNumber(requestDTO.getAccountNumber());
        paymentEntity.setDepositorName(requestDTO.getDepositorName());
        paymentEntity.setBankName(requestDTO.getBankName());
        paymentEntity.setDepositTime(requestDTO.getDepositTime());

        paymentRepository.save(paymentEntity);
    }

    public void confirmDeposit(String paymentId) {
        PaymentEntity paymentEntity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        paymentEntity.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(paymentEntity);
    }
}
