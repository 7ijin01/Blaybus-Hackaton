package com.blaybus.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "카카오페이 결제 승인 요청 DTO")
public class KakaoApproveRequestDTO {
    @Schema(description = "주문 ID")
    private String partnerOrderId;

    @Schema(description = "사용자 ID")
    private String partnerUserId;

    @Schema(description = "카카오에서 전달받은 pg_token")
    private String pgToken;
}
