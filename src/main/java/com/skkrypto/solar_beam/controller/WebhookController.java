package com.skkrypto.solar_beam.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhook") // 공통 경로 설정
public class WebhookController {

    // 로그 출력을 위한 Logger 객체 생성
    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

//    /**
//     * QuickNode의 솔라나 newBlock 웹훅을 수신하는 엔드포인트입니다.
//     * @param notification QuickNodded 받은 블록 데이터
//     * @return 성공적으로 수신했음을 알리는 HTTP 200 OK 응답
//     */
    @PostMapping("/solana-block")
    public ResponseEntity<String> handleSolanaBlockWebhook(@RequestBody String rawJson) {
        // 1. 수신된 데이터 로그 출력 (디버깅 및 확인용)
        logger.info("✅ Raw Webhook Body Received:\n{}", rawJson);



        // 2. 수신된 데이터를 활용한 비즈니스 로직 처리
        // 예: 데이터베이스에 블록 정보 저장, 특정 트랜잭션 감지, 다른 시스템에 알림 전송 등
        // processBlockData(notification);

        // 3. QuickNode에 정상적으로 수신했음을 알리기 위해 HTTP 200 OK 응답 반환
        return ResponseEntity.ok("Webhook received successfully.");
    }
}