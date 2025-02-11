package com.blaybus.dto;

import lombok.Data;

@Data
public class KakaoPayRequestDTO {  // 결제 요청에 필요한 데이터 담는 dto
    private String partnerOrderId;  // 주문 id
    private String partnerUserId;  // 사용자 id
    private String itemName;  // 상품명
    private String totalAmount;  // 총 금액
    private String vatAmount;  // 부가세
    private String taxFreeAmount;  // 비과세
    private String approvalUrl;  // 승인 url
    private String failUrl;  // 실패 url
    private String cancelUrl;  // 취소 url
}
