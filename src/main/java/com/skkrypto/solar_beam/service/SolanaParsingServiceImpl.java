package com.skkrypto.solar_beam.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skkrypto.solar_beam.dto.BlockDto;
import com.skkrypto.solar_beam.dto.SolanaTransactionDto;
import com.skkrypto.solar_beam.exception.SolanaParsingException;

public class SolanaParsingServiceImpl implements SolanaParsingService {

    private final ObjectMapper objectMapper;

    public SolanaParsingServiceImpl() {
        // ObjectMapper는 생성 비용이 높으므로, 재사용하도록 멤버 변수로 관리합니다.
        this.objectMapper = new ObjectMapper();
        // DTO에 정의되지 않은 JSON 필드가 있어도 파싱 에러가 나지 않도록 설정합니다.
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Object parse(String jsonString) throws SolanaParsingException {
        if (jsonString == null || jsonString.isBlank()) {
            throw new SolanaParsingException("입력된 JSON 문자열이 비어있습니다.");
        }

        try {
            // 1. 먼저 JSON 문자열을 범용 JsonNode 객체로 읽어들입니다.
            //    (문자열을 두 번 파싱하는 비효율을 막기 위함)
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 2. 특정 필드의 존재 여부로 JSON의 종류를 판별합니다.
            //    - "blockHeight" 필드가 있으면 Block 정보로 간주합니다.
            if (rootNode.has("blockHeight")) {
                return objectMapper.treeToValue(rootNode, BlockDto.class);
            }
            //    - "meta"와 "transaction" 필드가 함께 있으면 트랜잭션 정보로 간주합니다.
            else if (rootNode.has("meta") && rootNode.has("transaction")) {
                return objectMapper.treeToValue(rootNode, SolanaTransactionDto.class);
            }
            //    - 위의 두 경우에 모두 해당하지 않으면 지원하지 않는 형식입니다.
            else {
                throw new SolanaParsingException("지원하지 않는 형식의 JSON 데이터입니다.");
            }

        } catch (JsonProcessingException e) {
            // Jackson 라이브러리에서 발생한 예외를 우리가 정의한 예외로 감싸서 다시 던집니다.
            throw new SolanaParsingException("JSON 파싱에 실패했습니다.", e);
        }
    }
}
