package com.blaybus.reservation.controller;

import com.blaybus.reservation.dto.ReservationDateRequestDto;
import com.blaybus.reservation.dto.ReservationRequestDto;
import com.blaybus.reservation.dto.ReservationResponseDto;

import com.blaybus.reservation.entity.Reservation;
import com.blaybus.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController
{
    //TODO:로그인 후 유저정보 연동

    private final ReservationService reservationService;


    @PostMapping("/create")
    public ResponseEntity<ReservationResponseDto> createReservation(
            @RequestHeader("Authorization") String token,
            @RequestBody ReservationRequestDto requestDto
    )
    {
        return ResponseEntity.ok(reservationService.createReservation(token,requestDto));
    }

//    @PutMapping("/mode")
//    public ResponseEntity<ReservationResponseDto> UpdateMeet(
//            @RequestBody ReservationRequestDto request)
//    {
//        return ResponseEntity.ok(reservationService.updateReservationMeet(request));
//    }
//
//
//    @PutMapping("/{reservationId}/designer")
//    public ResponseEntity<ReservationResponseDto> selectDesigner(
//            @RequestBody ReservationRequestDto request)
//    {
//        return ResponseEntity.ok(reservationService.updateReservationDesigner(request));
//    }
//
//
//    @PutMapping("/{reservationId}/designer/date")
//    public ResponseEntity<ReservationResponseDto> selectDateAndTime(
//            @RequestBody ReservationRequestDto request)
//    {
//        return ResponseEntity.ok(reservationService.updateReservationDateAndTime(request));
//    }


    @GetMapping("/available")
    public ResponseEntity<ReservationResponseDto.ReservationTimeResponse> getReservationsByDesignerAndDate(
            @RequestParam LocalDate date,
            @RequestParam String designerId) {

        return ResponseEntity.ok(reservationService.getReservationsByDesignerAndDate(date,designerId));
    }



}
