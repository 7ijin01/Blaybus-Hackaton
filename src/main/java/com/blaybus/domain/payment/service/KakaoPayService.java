package com.blaybus.domain.payment.service;

import com.blaybus.KakaoPayProperties;
import com.blaybus.domain.payment.dto.KakaoApproveRequestDTO;
import com.blaybus.domain.payment.dto.KakaoApproveResponseDTO;
import com.blaybus.domain.payment.dto.KakaoPayReadyResponseDTO;
import com.blaybus.domain.payment.dto.KakaoPayRequestDTO;
import com.blaybus.domain.payment.entity.KakaoPayInfo;
import com.blaybus.domain.payment.entity.PaymentEntity;
import com.blaybus.domain.payment.entity.enums.PaymentMethod;
import com.blaybus.domain.payment.entity.enums.PaymentStatus;
import com.blaybus.domain.payment.repository.PaymentRepository;
import com.blaybus.domain.reservation.entity.Reservation;
import com.blaybus.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class KakaoPayService {

    private final PaymentRepository paymentRepository;
    private final KakaoPayProperties payProperties;
    private final RestTemplate restTemplate;
    private KakaoPayReadyResponseDTO kakaoReady;
    private final ReservationRepository reservationRepository;

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

        // 결제 정보 저장 코드 추가
        KakaoPayInfo kakaoPayInfo = KakaoPayInfo.builder()
                .tid(kakaoReady.getTid())  // 카카오페이 TID
                .aid(approveResponse.getAid())  // 요청 고유 번호
                .cid(payProperties.getCid())  // 가맹점 코드
                .partnerOrderId(requestDTO.getPartnerOrderId())  // 가맹점 주문 번호
                .partnerUserId(requestDTO.getPartnerUserId())  // 가맹점 회원 ID
                .paymentMethodType(approveResponse.getPayment_method_type())  // 결제 수단 (카드, 계좌이체 등)
                .itemName(approveResponse.getItem_name())  // 상품명
                .quantity(1)  // 단일 상품 결제이므로 수량 1
                .totalAmount(approveResponse.getAmount().getTotal())  // 총 금액
                .vatAmount(approveResponse.getAmount().getTax())  // 부가세
                .taxFreeAmount(approveResponse.getAmount().getTax_free())  // 비과세 금액
                .createdAt(LocalDateTime.now())  // 결제 요청 시간
                .approvedAt(LocalDateTime.now())  // 결제 승인 시간
                .build();


        // PaymentEntity에 결제 정보 저장
        PaymentEntity payment = PaymentEntity.builder()
                .userId(requestDTO.getPartnerUserId())  // 사용자 ID
                .reservationId(requestDTO.getPartnerOrderId())  // 예약 ID
                .status(PaymentStatus.SUCCESS)  // 결제 상태 SUCCESS
                .amount(Double.parseDouble(String.valueOf(approveResponse.getAmount().getTotal())))  // 결제 금액
                .paymentMethod(PaymentMethod.KAKAOPAY)  // 결제 방식 카카오페이
                .createdAt(LocalDateTime.now())  // 결제 생성 시간
                .kakaoPayInfo(kakaoPayInfo)  // 카카오페이 결제 정보 저장
                .build();

        Reservation reservation=reservationRepository.findOneById(payment.getReservationId());
        reservation.setStatus("CONFIRMED");

        reservationRepository.save(reservation);
        paymentRepository.save(payment);  // MongoDB에 저장

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
