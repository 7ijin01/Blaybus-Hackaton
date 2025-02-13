package com.blaybus.reservation.controller;

import com.blaybus.reservation.dto.ReservationRequestDto;
import com.blaybus.reservation.dto.ReservationResponseDto;

import com.blaybus.reservation.entity.Reservation;
import com.blaybus.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController
{
    //TODO:로그인 후 유저정보 연동

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/create")
    public ResponseEntity<ReservationResponseDto.ReservationResponse> createReservation(@RequestHeader("Authorization") String accessToken) {
        System.out.println(accessToken);
        return ResponseEntity.ok(reservationService.createReservation(accessToken));
    }

    @PutMapping("/{reservationId}/mode")
    public ResponseEntity<ReservationResponseDto.ReservationMeetResponse> UpdateMeet(
            @PathVariable String reservationId,
            @RequestBody ReservationRequestDto.ReservationMeetRequest request)
    {
        return ResponseEntity.ok(reservationService.updateReservationMeet(reservationId,request.isMeet()));
    }


    @PutMapping("/{reservationId}/designer")
    public ResponseEntity<ReservationResponseDto.ReservationDesignerResponse> selectDesigner(
            @PathVariable String reservationId,
            @RequestBody ReservationRequestDto.ReservationDesignerIdRequest request)
    {
        return ResponseEntity.ok(reservationService.updateReservationDesigner(reservationId,request));
    }


    @PutMapping("/{reservationId}/designer/date")
    public ResponseEntity<Reservation> selectDateAndTime(
            @PathVariable String reservationId,
            @RequestBody ReservationRequestDto.ReservationDateAndTimeRequest request)
    {
        return ResponseEntity.ok(reservationService.updateReservationDateAndTime(reservationId,request));
    }


    @GetMapping("/{designerId}/available")
    public ResponseEntity<ReservationResponseDto.ReservationTimeResponse> getReservationsByDesignerAndDate(
            @PathVariable String designerId,
            @RequestBody ReservationRequestDto.ReservationDateRequest request) {

        return ResponseEntity.ok(reservationService.getReservationsByDesignerAndDate(designerId, request));
    }



}
