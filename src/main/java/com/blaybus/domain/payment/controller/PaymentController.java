//package com.blaybus.domain.payment.controller;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/payment")
//@RequiredArgsConstructor
//@Slf4j
//public class PaymentController {
//
//    @GetMapping("/success")
//    public ResponseEntity<String> paymentSuccess(@RequestParam("pg_token") String pgToken) {
//        log.info("✅ [KakaoPay] 결제 성공: pg_token={}", pgToken);
//
//        // KakaoPay 결제 승인 로직 추가
//        return ResponseEntity.ok("결제 성공! pg_token=" + pgToken);
//    }
//}
