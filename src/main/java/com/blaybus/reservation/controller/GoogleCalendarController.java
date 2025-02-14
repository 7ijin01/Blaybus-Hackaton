package com.blaybus.reservation.controller;

import com.blaybus.reservation.dto.GoogleMeetRequest;
import com.blaybus.reservation.service.GoogleCalendarService;
import com.blaybus.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "구글 미팅 생성 API", description = "사용자의 구글 캘린더에 자동으로 구글 미팅 예약 생성 및 응답 바디로 구글 미팅 주소 리턴")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/google-calendar")
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;
    private final ReservationService reservationService;


//    @GetMapping("/calendar-ids")
//    public ResponseEntity<Map<String, String>> getCalendarIds(@RequestHeader("Authorization") String authorizationHeader) {
//
//        String accessToken = authorizationHeader.replace("Bearer ", "");
//        String calendarId = googleCalendarService.getCalendarIds(accessToken);
//        Map<String, String> response = new HashMap<>();
//        response.put("calendarId",calendarId);
//        return ResponseEntity.ok(response);
//
//    }

//    @PostMapping("/create-event")
//    public String createEvent(
//            @RequestHeader("Authorization") String authorizationHeader,
//            @RequestParam("calendarId") String calendarId) {
//
//        String accessToken = authorizationHeader.replace("Bearer ", "");
//        return googleCalendarService.createEvent(accessToken, calendarId);
//    }
    @Operation(summary = "구글 미팅 생성", description = "Authorization 헤더에는 jwt토큰이 아닌 구글 Oauth2 엑세스 토큰을 주입, 예약ID, 유저ID(구글메일주소), 회의 이름(ex: 미용실 컨설팅 예약),시작시간,종료시간(2025-02-19T19:30:00+09:00 이런식으로 한국 시간대에 맞게 요청해야함)을 요청body에 적고 요청하면 예약db에 저장 되고 응답 바디에 미팅 주소가 리턴됨")
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
