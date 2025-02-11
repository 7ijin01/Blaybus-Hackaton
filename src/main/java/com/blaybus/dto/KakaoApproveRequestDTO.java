package com.blaybus.dto;

import lombok.Data;

@Data
public class KakaoApproveRequestDTO {
    private String partnerOrderId;  // 주문 ID
    private String partnerUserId;  // 사용자 ID
    private String pgToken;  // 카카오에서 전달받은 pg_token
}
