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

    //    public ReservationResponseDto updateReservationMeet(ReservationRequestDto requestDto)
//    {
//        String reservationId= requestDto.getReservationId();
//        log.info("resID: {}", reservationId);
//        Reservation oldReservation = reservationRepository.findOneById(reservationId);
//        log.info("oldID:{}", oldReservation.getId());
//        if (oldReservation == null) {
//            throw new IllegalArgumentException("no reservation " + reservationId);
//        }
//        Reservation updatedReservation = Reservation.buildReservation(oldReservation, requestDto);
//        log.info("newID:{}", updatedReservation.getId());
//        return new ReservationResponseDto(updatedReservation);
//    }
//
//
//    public ReservationResponseDto updateReservationDesigner(ReservationRequestDto requestDto)
//    {
//        String reservationId= requestDto.getReservationId();
//        Reservation oldReservation = reservationRepository.findOneById(reservationId);
//        if (oldReservation == null) {
//            throw new IllegalArgumentException("no reservation " + reservationId);
//        }
//        String designerId=requestDto.getDesignerId();
//        Designer designer= designerService.getOneDesigner(designerId);
//        if (designer == null) {
//            throw new IllegalArgumentException("no designerss" + designerId);
//        }
//        requestDto.setPrice(requestDto.getMeet() == null ?
//                oldReservation.getPrice() :
//                (requestDto.getMeet()) ?
//                                designer.getPrice_meet() :
//                                designer.getPrice_not_meet())
//
//                ;
//        requestDto.setDesignerId(designerId);
//        requestDto.setShop(designer.getAddress());
//
//        Reservation updatedReservation = Reservation.buildReservation(oldReservation, requestDto);
//        reservationRepository.save(updatedReservation);
//
//
//        return new ReservationResponseDto(updatedReservation);
//    }
//
//
//    public ReservationResponseDto updateReservationDateAndTime(ReservationRequestDto requestDto)
//    {
//        String reservationId=requestDto.getReservationId();
//        Reservation oldReservation=reservationRepository.findOneById(reservationId);
//
//        LocalDate date=requestDto.getDate();
//        LocalTime start=requestDto.getStart();
//        LocalTime end= start.plusMinutes(30);
//
//        Reservation updatedReservation = Reservation.buildReservation(oldReservation, requestDto);
//
//        reservationRepository.save(updatedReservation);
//        return new ReservationResponseDto(updatedReservation);
//    }
//
//
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

        Set<String> reservedTimes = reservationRepository.findByDesignerIdAndDate(designerId, date);
        log.info("reservedTimes: {}", reservedTimes);

        List<String> availableTimes = new ArrayList<>();
        LocalTime time = LocalTime.of(10, 0);
        LocalTime closingTime = LocalTime.of(20, 0);

        while (time.isBefore(closingTime)) {
            if (!reservedTimes.contains(time.toString())) {
                availableTimes.add(time.toString());
            }
            time = time.plusMinutes(30);
        }
        log.info("availableTimes: {}", availableTimes);

        return new ReservationResponseDto.ReservationTimeResponse(date.toString(), availableTimes);
    }

    public List<Reservation> findReservationsByUserId(String accessToken){
        log.info("accessToken:{}", accessToken);
        String userId = jwtUtil.getEmail(accessToken.substring(7).trim());
        return reservationRepository.findAllByGoogleId(userId);
    }

    public String deleteReservation(String reservationId, String designerId){
        executeRefund(reservationRepository.findOneById(reservationId));
        log.info("target Reservation: {}", reservationRepository.findOneById(reservationId));
        return reservationRepository.deleteByDidAndRid(designerId, reservationId);
    }

    public void executeRefund(Reservation reservation){
        PaymentEntity payment = paymentRepository.findByReservationId(reservation.getId());
        switch (reservation.getMethod()){
            case "KAKAOPAY":
                kakaoPayService.kakaoCancel(findTidFromKakaoPayment(reservation.getId()));
                payment.setStatus(CANCELLED);
                paymentRepository.save(payment);
            case "BANK_TRANSFER":
                payment.setStatus(CANCELLED);
                paymentRepository.save(payment);
        }
    }

    public String findTidFromKakaoPayment(String reservationId){
        PaymentEntity payment = paymentRepository.findByReservationId(reservationId);
        return payment.getKakaoPayInfo().getTid();
    }
}
