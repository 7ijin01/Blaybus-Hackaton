package com.blaybus.reservation.service;

import com.blaybus.reservation.dto.GoogleMeetRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GoogleCalendarService {

    private static final String API_URL = "https://www.googleapis.com/calendar/v3/users/me/calendarList";
    private static final String API_EVENT_URL = "https://www.googleapis.com/calendar/v3/calendars/{calendarId}/events?conferenceDataVersion=1";
    private final RestTemplate restTemplate;

    public GoogleCalendarService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getCalendarIds(String accessToken) {
        List<String> calendarIds = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());

                JsonNode items = rootNode.get("items");
                if (items != null && items.isArray()) {
                    for (JsonNode item : items) {
                        String id = item.get("id").asText();
                        calendarIds.add(id);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: " + response.getStatusCode());
        }

        return calendarIds.get(1);
    }
//    public String createEvent(String accessToken, String calendarId) {
//
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("summary", "테스트");
//
//        Map<String, String> start = new HashMap<>();
//        start.put("dateTime", "2025-02-15T13:00:00");
//        start.put("timeZone", "Asia/Seoul");
//
//        Map<String, String> end = new HashMap<>();
//        end.put("dateTime", "2025-02-15T14:00:00");
//        end.put("timeZone", "Asia/Seoul");
//
//        requestBody.put("start", start);
//        requestBody.put("end", end);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + accessToken);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//
//
//        ResponseEntity<String> response = restTemplate.exchange(API_EVENT_URL, HttpMethod.POST, entity, String.class, calendarId);
//
//        if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
//            return "이벤트 생성 성공: " + response.getBody();
//        } else {
//            return "이벤트 생성 실패: " + response.getStatusCode();
//        }
//    }
    public String createEventWithMeeting(String accessToken, GoogleMeetRequest googleMeetRequest) {

        String calendarId= googleMeetRequest.getCalendarId();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("summary", googleMeetRequest.getSummary());

        Map<String, String> start = new HashMap<>();
        start.put("dateTime", googleMeetRequest.getStartTime());
        start.put("timeZone", "Asia/Seoul");

        Map<String, String> end = new HashMap<>();
        end.put("dateTime", googleMeetRequest.getEndTime());
        end.put("timeZone", "Asia/Seoul");

        requestBody.put("start", start);
        requestBody.put("end", end);


        Map<String, Object> conferenceData = new HashMap<>();
        Map<String, String> conferenceSolutionKey = new HashMap<>();
        conferenceSolutionKey.put("type", "hangoutsMeet");

        Map<String, Object> createRequest = new HashMap<>();
        createRequest.put("requestId", UUID.randomUUID().toString());
        createRequest.put("conferenceSolutionKey", conferenceSolutionKey);

        conferenceData.put("createRequest", createRequest);
        requestBody.put("conferenceData", conferenceData);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);



        ResponseEntity<String> response = restTemplate.exchange(API_EVENT_URL, HttpMethod.POST, entity, String.class, calendarId);

        if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());

                if (rootNode.has("hangoutLink")) {
                    return rootNode.get("hangoutLink").asText();
                }

                JsonNode conferenceDataNode = rootNode.get("conferenceData");
                if (conferenceDataNode != null && conferenceDataNode.has("entryPoints")) {
                    JsonNode entryPoints = conferenceDataNode.get("entryPoints");
                    if (entryPoints.isArray() && entryPoints.size() > 0) {
                        return entryPoints.get(0).get("uri").asText();
                    }
                }
                return "회의 링크를 찾을 수 없습니다.";
            } catch (Exception e) {
                e.printStackTrace();
                return "JSON 파싱 오류 발생";
            }
        } else {
            return "이벤트 생성 실패: " + response.getStatusCode();
        }
    }

}































