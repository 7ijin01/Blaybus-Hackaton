package com.blaybus.domain.payment.service;

import com.blaybus.domain.payment.dto.AmountDTO;
import com.blaybus.domain.payment.dto.BankTransferRequestDTO;
import com.blaybus.domain.payment.entity.BankTransferInfo;
import com.blaybus.domain.payment.entity.PaymentEntity;
import com.blaybus.domain.payment.entity.enums.PaymentMethod;
import com.blaybus.domain.payment.entity.enums.PaymentStatus;
import com.blaybus.domain.payment.repository.PaymentRepository;
import com.blaybus.domain.reservation.dto.ReservationRequestDto;
import com.blaybus.domain.reservation.entity.Reservation;
import com.blaybus.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BankTransferService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    // 계좌이체 결제 처리
    public String processBankTransfer(ReservationRequestDto requestDto, String reservationId, String userId) {
        // 계좌이체 정보 생성
        BankTransferInfo bankTransferInfo = BankTransferInfo.builder()
                .accountNumber("110-123-456789")  // 고객이 입금할 계좌번호
                .bankName("신한은행")  // 은행명
                .depositorName("Bliss(김아정)")  // 예금자명
                .build();


        // 결제 정보 생성
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .reservationId(reservationId)
                .userId(userId)
                .designerId(requestDto.getDesignerId())
                .amount(new AmountDTO())
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

        //예약 정보 업데이트
        Reservation reservation=reservationRepository.findOneById(paymentEntity.getReservationId());
        reservation.setStatus("CONFIRMED");
        reservation.setMethod(String.valueOf(PaymentMethod.BANK_TRANSFER));

        reservationRepository.save(reservation);
    }
}
