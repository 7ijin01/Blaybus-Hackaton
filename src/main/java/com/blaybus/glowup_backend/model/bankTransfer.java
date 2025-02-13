package com.blaybus.glowup_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class bankTransfer {
    private String accountNumber;
    private String depositorName;
    private String bankName;
    private Date depositTime;
}
