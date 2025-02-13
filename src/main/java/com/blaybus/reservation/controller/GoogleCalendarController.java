package com.blaybus.reservation.controller;

import com.blaybus.reservation.dto.GoogleMeetRequest;
import com.blaybus.reservation.service.GoogleCalendarService;
import com.blaybus.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/google-calendar")
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;
    private final ReservationService reservationService;


    public GoogleCalendarController(GoogleCalendarService googleCalendarService, ReservationService reservationService) {
        this.googleCalendarService = googleCalendarService;
        this.reservationService = reservationService;
    }

    @GetMapping("/calendar-ids")
    public ResponseEntity<Map<String, String>> getCalendarIds(@RequestHeader("Authorization") String authorizationHeader) {

        String accessToken = authorizationHeader.replace("Bearer ", "");
        String calendarId = googleCalendarService.getCalendarIds(accessToken);
        Map<String, String> response = new HashMap<>();
        response.put("calendarId",calendarId);
        return ResponseEntity.ok(response);

    }

//    @PostMapping("/create-event")
//    public String createEvent(
//            @RequestHeader("Authorization") String authorizationHeader,
//            @RequestParam("calendarId") String calendarId) {
//
//        String accessToken = authorizationHeader.replace("Bearer ", "");
//        return googleCalendarService.createEvent(accessToken, calendarId);
//    }
    @PostMapping("/create-event-with-meeting")
    public ResponseEntity<Map<String, String>> createEventWithMeeting(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody GoogleMeetRequest googleMeetRequest) {

        String accessToken = authorizationHeader.replace("Bearer ", "");
        String googleMeetUrl =googleCalendarService.createEventWithMeeting(accessToken, googleMeetRequest);
        reservationService.updateReservationGoogleMeetUri(googleMeetRequest.getReservationId(),googleMeetUrl);

        Map<String, String> response = new HashMap<>();
        response.put("meetingLink", googleMeetUrl);

        return ResponseEntity.ok(response);
    }


}
