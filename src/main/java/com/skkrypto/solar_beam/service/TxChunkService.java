package com.skkrypto.solar_beam.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;

@Service
public class TxChunkService {

    ObjectMapper objectMapper = new ObjectMapper();

    public TxChunkService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    // parsing

    // tx table

    // account

    // tx account

    // copy 로직을

    // csv 코피는 간다
    // binary copy 최고속 넘어려움
    //

    // 테이블별 버퍼

    public void processChunk(byte[] body, long slot, OffsetDateTime blockTime) throws IOException {
        JsonFactory jf = objectMapper.getFactory();

    }

}
