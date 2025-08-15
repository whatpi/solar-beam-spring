package com.skkrypto.solar_beam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skkrypto.solar_beam.dto.OrchestraRequestDto;
import com.skkrypto.solar_beam.service.OrchestraQuickNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.ByteBuffer;

@RestController
@RequestMapping("/api/webhook") // 공통 경로 설정
public class WebhookController {

    private final OrchestraQuickNodeService orchestraQuickNodeService;
    private final RabbitTemplate rabbitTemplate;

    // 로그 출력을 위한 Logger 객체 생성
    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    private static final String SAVE_DIRECTORY = "saved_blcoks";
    private final ObjectMapper objectMapper;

    public WebhookController(OrchestraQuickNodeService orchestraQuickNodeService, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.orchestraQuickNodeService = orchestraQuickNodeService;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }


    @PostMapping("/solana-block")
    public ResponseEntity<String> handleSolanaBlockWebhook(
            @RequestHeader("batch-start-range") Long slot,
            @RequestHeader("x-qn-signature") String signature,
            @RequestBody byte[] requestBody) {
        // 검증 로직을 도입할 수 있는데 많이 빡셈
        // byte 본문 값이랑 합치려면 inputstream에 제약이 생길 수 있다

        // 검증로직
        // validate(signature, body)

        logger.info("handleSolanaBlockWebhook");

        try {
//            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES + requestBody.length);
//            buffer.putLong(slot);
//            buffer.put(requestBody);

            rabbitTemplate.convertAndSend(
                    "solar-beam-exchange",
                    "orchestration",
//                    buffer.array(),
                    requestBody,
                    m -> {m.getMessageProperties().setHeader("slot", slot); return m;}
            );
            logger.info("✅ Slot " + slot + " published to ORCHESTRA queue.");
            return ResponseEntity.ok("Webhook received. Job queued for processing.");

        } catch (Exception e) {
            logger.error("Error processing webhook for slot {}", slot, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save webhook data.");
        }

//        orchestraQuickNodeService.orchestra(slot, inputStream);

//        logger.info("Webhook Headers Received:");
//        headers.forEach((key, value) -> logger.info("  {}: {}", key, value));

//        try {
//            Path savePath = Paths.get(SAVE_DIRECTORY);
//            if (!Files.exists(savePath)) {
//                Files.createDirectories(savePath);
//            }

//            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
//            String fileName = "solana-block-" + timestamp + ".json";
//
//            Path filePath = savePath.resolve(fileName);
//
//            long bytesCopied = Files.copy(inputStream, filePath);


//        // 2. 수신된 데이터를 활용한 비즈니스 로직 처리
//        // 예: 데이터베이스에 블록 정보 저장, 특정 트랜잭션 감지, 다른 시스템에 알림 전송 등
//        // processBlockData(notification);
//
//        // 3. QuickNode에 정상적으로 수신했음을 알리기 위해 HTTP 200 OK 응답 반환
//        return ResponseEntity.ok("Webhook received successfully.");
    }
}