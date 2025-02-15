package com.blaybus.domain.reservation.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleMeetRequest
{
    private String reservationId;

    private String userId;
    private String summary;
    private String startTime;
    private String endTime;

}
