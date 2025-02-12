package com.blaybus.glowup_backend.model;

import lombok.Data;

import java.util.Date;

@Data
public class Reservaion {
    private String userId;
    private String status;
    private Boolean meet;
    private String designerId;
    private Date date;
    private Date start;
    private Date end;
    private String shop;
    private String price;
    private Date expireAt;

}
