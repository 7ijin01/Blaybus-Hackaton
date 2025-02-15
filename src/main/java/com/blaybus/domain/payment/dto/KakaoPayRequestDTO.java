package com.blaybus.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "카카오페이 결제 요청 DTO")
public class KakaoPayRequestDTO {
    @Schema(description = "주문 ID")
    private String partnerOrderId;

    @Schema(description = "사용자 ID")
    private String partnerUserId;

    @Schema(description = "상품명")
    private String itemName;

    @Schema(description = "총 금액, totalAmount = vatAmount + taxFreeAmount")
    private String totalAmount;

    @Schema(description = "부가세")
    private String vatAmount;

    @Schema(description = "비과세 금액")
    private String taxFreeAmount;

    @Schema(description = "승인 URL")
    private String approvalUrl;

    @Schema(description = "실패 URL")
    private String failUrl;

    @Schema(description = "취소 URL")
    private String cancelUrl;
}
