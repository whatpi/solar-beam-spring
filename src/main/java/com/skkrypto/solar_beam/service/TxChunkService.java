package com.skkrypto.solar_beam.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skkrypto.solar_beam.dto.AccountKeyDto;
import com.skkrypto.solar_beam.dto.SolanaTransactionDto;
import com.skkrypto.solar_beam.exception.SolanaParsingException;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;


import java.io.IOException;
import java.io.StringWriter;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TxChunkService {
    CsvMapper csvMapper = new CsvMapper();
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

    public void processChunk(
            byte[] body,
            long slot,
            OffsetDateTime blockTime,
            int chunkIndex,
            int startTxIdx
    ) throws IOException {
        JsonFactory jf = objectMapper.getFactory();

//        List<SolanaTransactionDto> txlist = new ArrayList<>();

        try (JsonParser p = jf.createParser(body)) {

            if(p.currentToken() != JsonToken.START_ARRAY) {
                throw new SolanaParsingException("Expected a start array but got " + p.currentToken());
            }

            while (p.nextToken() != JsonToken.END_ARRAY) {
                SolanaTransactionDto tx = objectMapper.readValue(p, SolanaTransactionDto.class);
                processTx(tx, slot, blockTime, startTxIdx);
                startTxIdx++;
            }
        }
    }

    private void processTx(
            SolanaTransactionDto tx,
            long slot,
            OffsetDateTime blockTime,
            int currentIdx
    ) throws IOException {
        StringWriter writer = new StringWriter();

        List<String> sigs = tx.getTransaction().getSignatures();
        String recentBlockhash = tx.getTransaction().getMessage().getRecentBlockhash();
        long fee = tx.getMeta().getFee();
        long compute = tx.getMeta().getComputeUnitsConsumed();
        List<String> logMsgs = tx.getMeta().getLogMessages();

        csv_tx(sigs.get(0), currentIdx, slot, blockTime, recentBlockhash, fee, compute, logMsgs);

        List<String> accounts = processAccount(tx.getTransaction().getMessage().getAccountKeys(), sigs, blockTime);
    }



    private List<String> processAccount(List<AccountKeyDto> accountKeys, List<String> sigs, OffsetDateTime blockTime) {
        accountKeys.forEach(accountKey -> {})
    }

    /**
     * 파라미터로 받은 데이터와 CsvSchema를 이용해 transactions 테이블용 CSV 라인을 생성합니다.
     * CsvMapper가 모든 포매팅과 이스케이프 처리를 담당합니다.
     *
     * @return CSV 포맷으로 변환된 문자열 한 줄 (예: "sig1,1,123,...\n")
     * @throws JsonProcessingException CsvMapper가 변환에 실패할 경우 발생
     */

    private String csv_tx(
            String signature,
            int idxInBlock,
            long slot,
            OffsetDateTime blockTime,
            String recentBlockhash,
            long fee,
            long computeUnits,
            List<String> logMessages
    ) throws JsonProcessingException {

        // 1. CsvSchema의 컬럼명과 동일한 key를 사용하는 Map을 생성합니다.
        Map<String, Object> row = new LinkedHashMap<>();

        // 2. 스키마에 정의된 순서대로 Map에 데이터를 담습니다.
        row.put("primary_signature", signature);
        row.put("idx_in_block", idxInBlock);
        row.put("block_slot", slot);
        row.put("block_time", blockTime.toString()); // CsvMapper가 처리할 수 있도록 문자열로 변환
        row.put("recent_blockhash", recentBlockhash);
        row.put("fee", fee);
        row.put("compute_units_consumed", computeUnits);
        row.put("log_messages", formatPostgresArray(logMessages)); // 배열 포맷팅은 여전히 필요

        // 3. CsvMapper에게 스키마와 데이터를 넘겨주면, Mapper가 알아서 CSV 한 줄을 만들어줍니다.
        // writeValueAsString은 자동으로 줄바꿈(\n)까지 포함해줍니다.
        return csvMapper.writer(CsvSchemaProvider.TRANSACTIONS_SCHEMA)
                .writeValueAsString(row);
    }


    /**
     * 문자열 리스트를 PostgreSQL의 text 배열 형식(예: {"log1","log2"})으로 포맷합니다.
     */
    private String formatPostgresArray(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null; // CsvMapper는 null을 빈 문자열로 처리하므로 그대로 둡니다.
            // PostgreSQL COPY는 빈 문자열을 NULL로 인식합니다.
        }
        String content = list.stream()
                .map(s -> s.replace("\\", "\\\\").replace("\"", "\\\""))
                .collect(Collectors.joining("\",\"", "\"", "\""));
        return "{" + content + "}";
    }


}
