package com.blaybus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "계좌이체 결제 요청 DTO")
public class BankTransferRequestDTO {

    // 클라이언트가 결제 요청을 보낼 때 사용할 DTO
    @Schema(description = "예약 ID")
    private String reservationId;

    @Schema(description = "사용자 ID")
    private String userId;

    @Schema(description = "디자이너 ID")
    private String designerId;

    @Schema(description = "결제 금액")
    private double amount;

    @Schema(description = "계좌번호")
    private String accountNumber;

    @Schema(description = "입금자명")
    private String depositorName;

    @Schema(description = "은행명")
    private String bankName;
}