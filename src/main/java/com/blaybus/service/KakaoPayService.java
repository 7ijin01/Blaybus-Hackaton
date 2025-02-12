package com.blaybus.service;

import com.blaybus.KakaoPayProperties;
import com.blaybus.dto.KakaoApproveRequestDTO;
import com.blaybus.dto.KakaoApproveResponseDTO;
import com.blaybus.dto.KakaoPayReadyResponseDTO;
import com.blaybus.dto.KakaoPayRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class KakaoPayService {

    private final KakaoPayProperties payProperties;
    private final RestTemplate restTemplate;
    private KakaoPayReadyResponseDTO kakaoReady;

    // https://developers.kakaopay.com/docs/payment/online/single-payment#payment-ready-request-syntax
    private HttpHeaders getHeaders() {  // 카카오페이 api : 헤더 설정 (Request Syntax 참고)
        HttpHeaders headers = new HttpHeaders();
        String auth = "SECRET_KEY " + payProperties.getSecretKey();  // "SECRET_KEY " 앞에 공백 필수
        headers.set("Authorization", auth);
        headers.set("Content-Type", "application/json");

        return headers;
    }

    /*
     * 결제 준비 요청 보내기
     * */
    // https://developers.kakaopay.com/docs/payment/online/single-payment#payment-ready-sample-request
    public KakaoPayReadyResponseDTO kakaoPayReady(KakaoPayRequestDTO requestDTO) {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("cid", payProperties.getCid());
        parameters.put("partner_order_id", requestDTO.getPartnerOrderId());
        parameters.put("partner_user_id", requestDTO.getPartnerUserId());
        parameters.put("item_name", requestDTO.getItemName());
        parameters.put("quantity", "1");  // 수량 -> 단일 결제니까 1로 냅둬야 하나...?
        parameters.put("total_amount", requestDTO.getTotalAmount());

        parameters.put("vat_amount", requestDTO.getVatAmount());
        parameters.put("tax_free_amount", requestDTO.getTaxFreeAmount());

        parameters.put("approval_url", requestDTO.getApprovalUrl());
        parameters.put("fail_url", requestDTO.getFailUrl());
        parameters.put("cancel_url", requestDTO.getCancelUrl());

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());


        // 외부에 보낼 url
        kakaoReady = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                requestEntity,
                KakaoPayReadyResponseDTO.class);

        log.info("카카오페이 결제 준비 응답: {}", kakaoReady);  // 응답 확인
        return kakaoReady;
    }


    /**
     * 결제 완료 승인
     */
    public KakaoApproveResponseDTO approveResponse(KakaoApproveRequestDTO requestDTO) {

        // 카카오 결제 승인 요청
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", payProperties.getCid());
        parameters.put("tid", kakaoReady.getTid());  // 이전에 준비된 TID
        parameters.put("partner_order_id", requestDTO.getPartnerOrderId());
        parameters.put("partner_user_id", requestDTO.getPartnerUserId());
        parameters.put("pg_token", requestDTO.getPgToken());

        // 파라미터, 헤더 설정
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());
        System.out.println();
        System.out.println();
        System.out.println(requestEntity);
        System.out.println();
        System.out.println();

        // 외부에 보낼 url
        KakaoApproveResponseDTO approveResponse = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/approve",
                requestEntity,
                KakaoApproveResponseDTO.class);

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(approveResponse);
        System.out.println();
        System.out.println();
        System.out.println();

        return approveResponse;
    }

}
