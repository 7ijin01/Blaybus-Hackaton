package com.blaybus.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankTransferInfo {
    private String accountNumber;  // 지정된 계좌번호
    private String bankName;  // 은행명
    private String depositorName;  // 예금자명
    private LocalDateTime depositTime;  // 입금 시간 (고객이 입금한 시간)
}
