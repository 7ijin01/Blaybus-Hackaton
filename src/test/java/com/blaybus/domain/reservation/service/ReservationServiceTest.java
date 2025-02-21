package com.blaybus.domain.reservation.service;

import com.blaybus.domain.payment.entity.PaymentEntity;
import com.blaybus.domain.payment.repository.PaymentRepository;
import com.blaybus.domain.payment.service.BankTransferService;
import com.blaybus.domain.payment.service.KakaoPayService;
import com.blaybus.domain.reservation.dto.ReservationRequestDto;
import com.blaybus.domain.reservation.dto.ReservationResponseDto;
import com.blaybus.domain.reservation.entity.Reservation;
import com.blaybus.domain.reservation.repository.DesignerRepository;
import com.blaybus.domain.reservation.repository.ReservationRepository;
import com.blaybus.global.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 환경 설정
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private DesignerRepository designerRepository;

    @Mock
    private DesignerService designerService;

    @Mock
    private KakaoPayService kakaoPayService;

    @Mock
    private BankTransferService bankTransferService;

    @Mock
    private JwtUtil jwtUtil;

    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        // 테스트용 예약 데이터 생성
        testReservation = Reservation.builder()
                .id("12345")
                .userId("test-user")
                .method("KAKAOPAY")
                .createdAt(Date.from(Instant.now()))
                .build();
    }

    // ✅ 1. 예약 생성 테스트
    @Test
    void testCreateReservation() {
        // given
        ReservationRequestDto requestDto = new ReservationRequestDto();
        requestDto.setMethod("KAKAOPAY");

        when(jwtUtil.getEmail(anyString())).thenReturn("test-user");
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // when
        ReservationResponseDto response = reservationService.createReservation("Bearer token", requestDto);

        // then
        assertNotNull(response);
        assertEquals("test-user", response.getUserId());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    // ✅ 2. 예약 삭제 테스트
    @Test
    void testDeleteReservation() {
        // given
        when(reservationRepository.findOneById("12345")).thenReturn(testReservation);
        when(reservationRepository.deleteByRid("12345")).thenReturn("deleted");

        // when
        String result = reservationService.deleteReservation("12345");

        // then
        assertEquals("deleted", result);
        verify(reservationRepository, times(1)).deleteByRid("12345");
    }



    // ✅ 4. 예약 취소 및 환불 테스트
    @Test
    void testExecuteRefund_KakaoPay() {
        // given
        PaymentEntity payment = new PaymentEntity();
        when(paymentRepository.findByReservationId("12345")).thenReturn(payment);

        testReservation.setMethod("KAKAOPAY");

        // when
        reservationService.executeRefund(testReservation);

        // then
        verify(kakaoPayService, times(1)).kakaoCancel(payment);
        verify(paymentRepository, times(1)).save(payment);
    }
}