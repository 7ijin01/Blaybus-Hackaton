package com.blaybus.reservation.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleMeetRequest
{

    private String summary;
    private String startTime;
    private String endTime;

}
