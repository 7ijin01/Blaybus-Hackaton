package com.blaybus.reservation.controller;

import com.blaybus.reservation.dto.ReservationMeetRequest;
import com.blaybus.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservation")
public class ReservationController
{
    //TODO:로그인 후 유저정보 연동

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createReservation() {
        String reservationId= reservationService.createReservation();
        return ResponseEntity.ok("Reservation id: " + reservationId);
    }

    @PutMapping("/{reservationId}/mode")
    public ResponseEntity<String> UpdateMeet(
            @PathVariable String reservationId,
            @RequestBody ReservationMeetRequest request)
    {
        reservationService.updateReservationMeet(reservationId,request.isMeet());
        return ResponseEntity.ok("Reservation id: " + reservationId);
    }
    @PutMapping("/{reservationId}/designer")
    public ResponseEntity<String> selectDeginer(
            @PathVariable String reservationId,
            @RequestParam String designerId)
    {
        reservationService.updateReservationDesigner(reservationId,designerId);
        return ResponseEntity.ok("Reservation id: " + reservationId);
    }

}
