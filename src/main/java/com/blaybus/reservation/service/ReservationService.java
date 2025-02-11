package com.blaybus.reservation.service;

import com.blaybus.global.jwt.JwtUtil;
import com.blaybus.reservation.dto.ReservationRequestDto;
import com.blaybus.reservation.dto.ReservationResponseDto;
import com.blaybus.reservation.entity.Designer;
import com.blaybus.reservation.entity.Reservation;
import com.blaybus.reservation.repository.DesignerRepository;
import com.blaybus.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService
{
    private final ReservationRepository reservationRepository;
    private final DesignerRepository designerRepository;
    private final DesignerService designerService;
    private final JwtUtil jwtUtil;
    //유저 코드 생성이후 연동할 예정

    public ReservationResponseDto.ReservationResponse createReservation(ReservationRequestDto.ReservationRequest request) {
        //String userName = jwtUtil.getName(accessToken);
        //String googleId = jwtUtil.getEmail(accessToken);
        Designer designer = designerService.getOneDesigner(request.getDesignerId());
        System.out.println(request.getDesignerId());
        System.out.println(designer);

        Map<String, String> newReservation = new HashMap<>();
        newReservation.put("start", request.getStart()); // "HH:mm" 형태의 시작 시간
        newReservation.put("end", request.getEnd());     // "HH:mm" 형태의 끝나는 시간



        Reservation reservation = new Reservation();
        reservation.setUserId("103066378037959369997");


        reservation.setId(UUID.randomUUID().toString());
        reservation.setStatus("PENDING");
        String reserveId = reservationRepository.save(reservation).getId();
        designer.getTimeTable().add(reserveId);
        designerRepository.save(designer);
        return new ReservationResponseDto.ReservationResponse(reservation.getId(),reservation.getStatus());
    }

    public ReservationResponseDto.ReservationMeetResponse updateReservationMeet(String reservationId, Boolean meet)
    {
        Reservation reservation = reservationRepository.findOneById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("no reservation " + reservationId);
        }
        reservation.setMeet(meet);
        reservationRepository.save(reservation);
        return new ReservationResponseDto.ReservationMeetResponse(reservationId,meet);
    }


    public ReservationResponseDto.ReservationDesignerResponse updateReservationDesigner(String reservationId, ReservationRequestDto.ReservationDesignerIdRequest request)
    {
        Reservation reservation = reservationRepository.findOneById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("no reservation " + reservationId);
        }
        String designerId=request.getDesignerId();
        Designer designer= designerService.getOneDesigner(designerId);
        if (designer == null) {
            throw new IllegalArgumentException("no designerss" + designerId);
        }

        reservation.setDesignerId(designerId);
        reservation.setShop(designer.getAddress());
        reservation.setPrice(reservation.getMeet() ?
                designer.getPrice_meet().toString() :
                designer.getPrice_not_meet().toString());
        reservationRepository.save(reservation);

        return new ReservationResponseDto.ReservationDesignerResponse(reservationId,designerId,reservation.getShop(),reservation.getPrice());
    }


    public ReservationResponseDto.ReservationTimeResponse getReservationsByDesignerAndDate(
            String designerId, ReservationRequestDto.ReservationDateRequest request) {

        LocalDate date = request.getDate();

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
    public Reservation updateReservationDateAndTime(String reservationId, ReservationRequestDto.ReservationDateAndTimeRequest request)
    {
        Reservation reservation=reservationRepository.findOneById(reservationId);

        LocalDate date=request.getDate();
        LocalTime time=request.getTime();
        LocalTime end= time.plusMinutes(30);

        reservation.setDate(date);
        reservation.setStart(time);
        reservation.setEnd(end);
        reservationRepository.save(reservation);
        return reservation;
    }

    public boolean deleteReservationById(String designerId, String reservationId){

        Reservation reservation = findReservationById(reservationId);
        Designer designer = designerService.getOneDesigner(designerId);

        designer.getTimeTable().remove(reservationId);
        designerService.processUpdateTimeTable(designer);
        reservationRepository.delete(reservation);
        return true;
    }


    public Reservation findReservationById(String reservationId){
        return reservationRepository.findById(reservationId)
                .orElseThrow(()->new RuntimeException());
    }
}
