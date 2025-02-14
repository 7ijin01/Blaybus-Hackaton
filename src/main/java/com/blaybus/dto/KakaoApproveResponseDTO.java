package com.blaybus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "카카오페이 결제 승인 응답 DTO")
public class KakaoApproveResponseDTO {
    @Schema(description = "요청 고유 번호")
    private String aid;

    @Schema(description = "결제 고유 번호")
    private String tid;

    @Schema(description = "가맹점 코드")
    private String cid;

    @Schema(description = "정기결제용 ID")
    private String sid;

    @Schema(description = "가맹점 주문 번호")
    private String partner_order_id;

    @Schema(description = "가맹점 회원 ID")
    private String partner_user_id;

    @Schema(description = "결제 수단")
    private String payment_method_type;

    @Schema(description = "결제 금액 정보")
    private AmountDTO amount;

    @Schema(description = "상품명")
    private String item_name;

    @Schema(description = "상품 코드")
    private String item_code;

    @Schema(description = "상품 수량")
    private int quantity;

    @Schema(description = "결제 요청 시간")
    private String created_at;

    @Schema(description = "결제 승인 시간")
    private String approved_at;

    @Schema(description = "결제 승인 요청에 대해 저장 값, 요청 시 전달된 내용")
    private String payload;
}
