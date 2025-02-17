package com.blaybus.domain.reservation.service;

import com.blaybus.domain.payment.entity.PaymentEntity;
import com.blaybus.domain.payment.repository.PaymentRepository;
import com.blaybus.domain.payment.service.BankTransferService;
import com.blaybus.domain.payment.service.KakaoPayService;
import com.blaybus.global.jwt.JwtUtil;
import com.blaybus.domain.reservation.exception.CustomException;
import com.blaybus.domain.reservation.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.blaybus.domain.reservation.dto.ReservationRequestDto;
import com.blaybus.domain.reservation.dto.ReservationResponseDto;
import com.blaybus.domain.reservation.entity.Designer;
import com.blaybus.domain.reservation.entity.Reservation;
import com.blaybus.domain.reservation.repository.ReservationRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.blaybus.domain.payment.entity.enums.PaymentStatus.CANCELLED;
import static javax.print.attribute.standard.JobState.CANCELED;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService
{
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final DesignerService designerService;
    private final KakaoPayService kakaoPayService;
    private final BankTransferService bankTransferService;
    private final JwtUtil jwtUtil;

    //유저 코드 생성이후 연동할 예정
    public ReservationResponseDto createReservation(String accessToken, ReservationRequestDto requestDto) {
        String googleId = jwtUtil.getEmail(accessToken);
        Reservation reservation=Reservation.buildReservation(requestDto);
        reservation.setUserId(googleId);
        reservation.setStatus("PENDING");
        reservation.setCreatedAt(Date.from(Instant.now()));
        reservationRepository.save(reservation);

        return new ReservationResponseDto(reservation);


    }


    public void updateReservationGoogleMeetUri(String reservationId,String googleMeetUri)
    {
        Reservation reservation=reservationRepository.findOneById(reservationId);
        if (reservation == null) {
            throw new CustomException(ExceptionCode.RESERVATION_NOT_FOUND);
        }
        reservation.setGoogleMeetUri(googleMeetUri);
        reservationRepository.save(reservation);
    }

    public ReservationResponseDto.ReservationTimeResponse getReservationsByDesignerAndDate(
            LocalDate date, String designerId) {

        List<LocalTime> availableTimes = new ArrayList<>(List.of(
                LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30),
                LocalTime.of(12, 0), LocalTime.of(12, 30), LocalTime.of(13, 0), LocalTime.of(13, 30),
                LocalTime.of(14, 0), LocalTime.of(14, 30), LocalTime.of(15, 0), LocalTime.of(15, 30),
                LocalTime.of(16, 0), LocalTime.of(16, 30), LocalTime.of(17, 0), LocalTime.of(17, 30),
                LocalTime.of(18, 0), LocalTime.of(18, 30), LocalTime.of(19, 0), LocalTime.of(19, 30)
        ));

        Set<LocalTime> reservedTimes = reservationRepository.findByDesignerIdAndDate(designerId, date)
                .stream()
                .map(LocalTime::parse)
                .collect(Collectors.toSet());

        availableTimes.removeAll(reservedTimes);

        List<String> availableTimesStr = availableTimes.stream()
                .map(LocalTime::toString)
                .collect(Collectors.toList());

        return new ReservationResponseDto.ReservationTimeResponse(date.toString(), availableTimesStr);
    }

    public List<Reservation> findReservationsByUserId(String accessToken){
        log.info("accessToken:{}", accessToken);
        String userId = jwtUtil.getEmail(accessToken.substring(7).trim());
        return reservationRepository.findAllByGoogleId(userId);
    }

    public String deleteReservation(String reservationId){
        log.info( "target Reservation: {}", reservationRepository.findOneById(reservationId));
        executeRefund(reservationRepository.findOneById(reservationId));
        log.info("target Reservation: {}", reservationRepository.findOneById(reservationId));
        return reservationRepository.deleteByRid(reservationId);
    }

    public void executeRefund(Reservation reservation){
        PaymentEntity payment = paymentRepository.findByReservationId(reservation.getId());
        switch (reservation.getMethod()){
            case "KAKAOPAY":
                kakaoPayService.kakaoCancel(payment);
                payment.setStatus(CANCELLED);
                paymentRepository.save(payment);
                break;

            case "BANK_TRANSFER":
                payment.setStatus(CANCELLED);
                paymentRepository.save(payment);
                break;
        }
    }

}
