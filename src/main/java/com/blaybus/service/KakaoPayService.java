package com.blaybus.service;

import com.blaybus.KakaoPayProperties;
import com.blaybus.dto.KakaoPayReadyResponseDTO;
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
    private KakaoPayReadyResponseDTO kakaoReady;

    // https://developers.kakaopay.com/docs/payment/online/single-payment#payment-ready-request-syntax
    private HttpHeaders getHeaders() {  // 카카오페이 api : Request Syntax 참고
        HttpHeaders headers = new HttpHeaders();
        String auth = "SECRET_KEY " + payProperties.getSecretKey();  // "SECRET_KEY " 앞에 공백 필수
        headers.set("Authorization", auth);
        headers.set("Content-Type", "application/json");

        return headers;
    }

    /*
     * 아래 예시에 맞게 결제 요청 보내기
     * */
    // https://developers.kakaopay.com/docs/payment/online/single-payment#payment-ready-sample-request
    public KakaoPayReadyResponseDTO kakaoPayReady() {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("cid", payProperties.getCid());
        parameters.put("partner_order_id", "1");  // 실제 주문 번호로 교체
        parameters.put("partner_user_id", "1");  // 실제 사용자 id로 교체
        parameters.put("item_name", "ITEM_NAME");  // 실제 상품명으로 교체
        parameters.put("quantity", "1");  // 수량, 숫자는 문자열로 전달
        parameters.put("total_amount", "1200");  // 총 금액, 숫자는 문자열로 전달

        parameters.put("vat_amount", "200");  // 부가세, 숫자는 문자열로 전달
        parameters.put("tax_free_amount", "0");  // 비과세 금액, 숫자는 문자열로 전달

        parameters.put("approval_url", "https://localhost:8080/success");  // 등록한 url 넣기
        parameters.put("fail_url", "https://localhost:8080/fail");
        parameters.put("cancel_url", "https://localhost:8080/cancel");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());


        // 외부에 보낼 url
        RestTemplate restTemplate = new RestTemplate();

        kakaoReady = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                requestEntity,
                KakaoPayReadyResponseDTO.class);

        log.info("카카오페이 결제 준비 응답: {}", kakaoReady);  // 응답 확인
        return kakaoReady;
    }
}
