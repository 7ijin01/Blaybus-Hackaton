package com.blaybus.domain.reservation.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import com.blaybus.domain.reservation.dto.ReservationRequestDto;
import com.blaybus.domain.reservation.dto.ReservationResponseDto;

import com.blaybus.domain.reservation.entity.Reservation;
import com.blaybus.domain.reservation.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
@Tag(name = "예약  API", description = "예약 생성 및 디자이너 시간대 조회")
@Slf4j
public class ReservationController
{
    //TODO:로그인 후 유저정보 연동

    private final ReservationService reservationService;

    @Operation(summary = "예약 생성", description = "리엑트 단에서 (대면,비대면)값, 디자이너 선택 및 시간 선택 결과값을 가지고 있다가 한번에 예약 요청")
    @PostMapping("/create")
    public ResponseEntity<ReservationResponseDto> createReservation(
            @RequestHeader("Authorization") String token,
            @RequestBody ReservationRequestDto requestDto
    )
    {
        return ResponseEntity.ok(reservationService.createReservation(token,requestDto));
    }

    /*
    @PutMapping("/{reservationId}/mode")
    public ResponseEntity<ReservationResponseDto.ReservationMeetResponse> UpdateMeet(
            @PathVariable String reservationId,
            @RequestBody ReservationRequestDto.ReservationMeetRequest request)
    {
        return ResponseEntity.ok(reservationService.createReservation(token,requestDto));
    }*/

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

    @Operation(summary = "디자이너 별 가능시간 조회", description = "2025-02-13와 같은 날짜와 designerId 를 넘겨주면 예약DB에서 검색 후 예약 가능한 시간대(String 형식의 날짜와 List 형태의 가능 시간리스트)를 리턴  시간리스트는 10:00 부터 19:30까지 가능한 시간만 포함되어있음 ")
    @GetMapping("/available")
    public ResponseEntity<ReservationResponseDto.ReservationTimeResponse> getReservationsByDesignerAndDate(
            @RequestParam LocalDate date,
            @RequestParam String designerId) {

        return ResponseEntity.ok(reservationService.getReservationsByDesignerAndDate(date,designerId));
    }

    @Operation(summary = "사용자별 예약 정보 조회")
    @GetMapping("/user")
    public ResponseEntity<?> readList(@RequestHeader("Authorization") String accessToken) {
        return ResponseEntity.ok(reservationService.findReservationsByUserId(accessToken));
    }

    @Operation(summary = "예약 취소")
    @DeleteMapping("")
    public ResponseEntity<String> delete(
            //@RequestHeader("Authorization") String accessToken
            @RequestParam String reservationId,
            @RequestParam String designerId) {
        return ResponseEntity.ok(reservationService.deleteReservation(reservationId, designerId));
    }
}
