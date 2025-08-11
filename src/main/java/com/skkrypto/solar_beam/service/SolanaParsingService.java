package com.skkrypto.solar_beam.service;

import com.skkrypto.solar_beam.exception.SolanaParsingException;

public interface SolanaParsingService {

    /**
     * JSON 문자열을 입력받아 내용을 분석한 후,
     * 적절한 DTO (BlockDto 또는 SolanaTransactionDto) 객체로 파싱하여 반환합니다.
     *
     * @param jsonString 파싱할 JSON 데이터
     * @return 파싱된 DTO 객체 (Object 타입이므로, 호출 후 instanceof로 타입 확인 및 캐스팅 필요)
     * @throws SolanaParsingException 유효하지 않은 JSON 형식이거나 지원하지 않는 구조일 경우 발생
     */
    Object parse(String jsonString) throws SolanaParsingException;
}
