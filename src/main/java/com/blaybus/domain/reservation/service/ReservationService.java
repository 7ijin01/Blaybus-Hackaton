package com.blaybus.domain.reservation.service;

import com.blaybus.domain.payment.entity.PaymentEntity;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService
{
    private final ReservationRepository reservationRepository;
    private final DesignerService designerService;
    private final JwtUtil jwtUtil;

    private final MongoTemplate mongoTemplate;
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

        List<String> availableTimes = new ArrayList<>();
        LocalTime time = LocalTime.of(10, 0);
        LocalTime closingTime = LocalTime.of(20, 0);

        while (time.isBefore(closingTime)) {
            if (!reservedTimes.contains(time.toString())) {
                availableTimes.add(time.toString());
            }
            time = time.plusMinutes(30);
        }

        return new ReservationResponseDto.ReservationTimeResponse(date.toString(), availableTimes);
    }
    public List<Reservation> findByUserId(String userId
                                          //String accessToken
                                          ) {
        // 실제 서비스 시에는 아래 코드 사용.
        //String extractedUserId = jwtUtil.getEmail(accessToken);
        log.info("findByUserId userId={}", userId);

        if (userId == null) {
            log.warn("read; userId is invalid");
            return new ArrayList<>();
        }
        List<Reservation> list = mongoTemplate.find(
                new Query(Criteria.where("userId").is(userId)), Reservation.class); // 기준 잡아서 시간 순으로 조회 되는 부분 추가하기
        log.info("list={}", list);
        return list;
    }

    public boolean delete(String reservationsId, String designerId) {
        log.info("delete reservationsId={}, designerId={}", reservationsId, designerId);

        if (reservationsId == null || designerId == null) {
            log.warn("delete; reservationsId or designerId is invalid");
            return false;
        }
        try {
            // 1. 예약 상태 '취소'로 변경
            cancelReservation(reservationsId);
            // 2. 디자이너 타임테이블에 반영
            removeFromDesignerTimeTable(reservationsId, designerId);
            // 3. 결제 취소
//            cancelPayment(reservationsId);

            // 4. 디자이너에게 알림 전송 (구현되면 좋을것 같음)

        } catch (Exception e) {
            log.error("예약 취소 시 에러 발생={}", e.getMessage());
            return false;
        }
        return true;
    }

    public int update(String reservationsId, String designerId) {
        log.info("update reservationsId={}, designerId={}", reservationsId, designerId);

        if (reservationsId == null || designerId == null) {
            log.warn("update; reservationsId or designerId is invalid");
            return 0;
        }
        // 1. 시간 변경 -> 디자이너 타임테이블에 반영

        return 1;
    }

    // 예약 상태를 업데이트하는 메서드
    private void updateStatus(String collectionName, String reservationsId, String status) {
        Query query = new Query(Criteria.where("reservationsId").is(reservationsId));
        Update update = new Update().set("status", status);
        mongoTemplate.updateFirst(query, update, collectionName);
    }

    // 예약 취소
    private void cancelReservation(String reservationsId) {
        updateStatus("reservations", reservationsId, "CANCEL");
    }

    // 결제 정보 조회
    private PaymentEntity findPaymentByReservationsId(String reservationsId) {
        // payments 컬렉션에서 reservationsId로 결제 정보 조회
        Query query = new Query(Criteria.where("reservationsId").is(reservationsId));
        return mongoTemplate.findOne(query, PaymentEntity.class, "payments");
    }

    // 결제 취소
    private void cancelPayment(String reservationsId) {
        PaymentEntity payment = findPaymentByReservationsId(reservationsId);

        // 카카오 페이 결제 취소 시
        if ("KAKAOPAY".equals(payment.getPaymentMethod())) {

        }

        // 결제 상태 변경
        updateStatus("payments", reservationsId, "CANCEL");

    }

    private void removeFromDesignerTimeTable(String reservationsId, String designerId) {
        // 1. 예약 정보 조회 (start, end 시간 가져오기)
        Reservation reservation = mongoTemplate.findOne(
                new Query(Criteria.where("reservationsId").is(reservationsId)
                        .and("designerId").is(designerId)), Reservation.class, "reservations");

        if (reservation == null) {
            throw new RuntimeException("No reservation found for reservationsId=" + reservationsId);
        }

        //Date start = reservation.getStart();
        //Date end = reservation.getEnd();
        // 2. 디자이너 컬렉션 조회
        Designer designers = mongoTemplate.findOne(
                new Query(Criteria.where("designerId").is(designerId)), Designer.class, "designers");

        if (designers == null) {
            throw new RuntimeException("No designer found with designerId=" + designerId);
        }

        // 3. timeTable에서 start와 end 사이에 포함되는 시간을 제외한 나머지 시간만 남기기
//        List<String> updatedTimeTable = designers.getTimeTable().stream()
//                .map(time -> parseDate(time))
//                .filter(timeDate -> timeDate.before(start) || timeDate.after(end)) // start ~ end 범위 제거
//                .map(this::formatDate) // 다시 문자열 변환
//                .collect(Collectors.toList());

        // 4. 디자이너 timeTable 업데이트
//        mongoTemplate.updateFirst(
//                new Query(Criteria.where("designerId").is(designerId)),
//                new Update().set("timeTable", updatedTimeTable),
//                Designer.class
//        );

        //log.info("Updated designer timeTable for designerId={}, removed times between {} and {}", designerId, start, end);
    }

    private Date parseDate(String time) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time);
        } catch (ParseException e) {
            throw new RuntimeException("Time format error: " + time);
        }
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }
}
