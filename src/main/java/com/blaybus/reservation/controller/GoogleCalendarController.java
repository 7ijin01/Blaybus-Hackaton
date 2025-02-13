package com.blaybus.reservation.controller;

import com.blaybus.reservation.dto.GoogleMeetRequest;
import com.blaybus.reservation.service.GoogleCalendarService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/google-calendar")
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;

    public GoogleCalendarController(GoogleCalendarService googleCalendarService) {
        this.googleCalendarService = googleCalendarService;
    }

    @GetMapping("/calendar-ids")
    public String getCalendarIds(@RequestHeader("Authorization") String authorizationHeader) {

        String accessToken = authorizationHeader.replace("Bearer ", "");
        return googleCalendarService.getCalendarIds(accessToken);
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
    public String createEventWithMeeting(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("calendarId") String calendarId,
            @RequestBody GoogleMeetRequest googleMeetRequest) {

        String accessToken = authorizationHeader.replace("Bearer ", "");
        return googleCalendarService.createEventWithMeeting(accessToken, calendarId,googleMeetRequest);
    }

}
