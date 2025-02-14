package com.blaybus.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "카카오페이 결제 준비 응답 DTO")
public class KakaoPayReadyResponseDTO {
    @Schema(description = "결제 고유 번호")
    private String tid;

    @Schema(description = "앱 리디렉트 URL")
    private String next_redirect_app_url;

    @Schema(description = "모바일 리디렉트 URL")
    private String next_redirect_mobile_url;

    @Schema(description = "PC 리디렉트 URL")
    private String next_redirect_pc_url;

    @Schema(description = "안드로이드 앱 스키마")
    private String android_app_scheme;

    @Schema(description = "iOS 앱 스키마")
    private String ios_app_scheme;

    @Schema(description = "결제 생성 시간")
    private String created_at;
}
