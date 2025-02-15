package com.blaybus.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "카카오페이 결제 금액 정보 DTO")
public class AmountDTO {
    @Schema(description = "총 결제 금액")
    private int total;

    @Schema(description = "비과세 금액")
    private int tax_free;

    @Schema(description = "부가세 금액")
    private int tax;

    @Schema(description = "사용한 포인트")
    private int point;

    @Schema(description = "할인 금액")
    private int discount;

    @Schema(description = "컵 보증금")
    private int green_deposit;
}
