package com.skkrypto.solar_beam.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class CsvFomatter {

    CsvMapper csvMapper = new CsvMapper();

    public CsvFomatter() {
        this.csvMapper = new CsvMapper();
    }

    /**
     * accounts 테이블의 한 행에 해당하는 데이터를 CSV 문자열로 변환합니다.
     *
     * @param pubkey 계정의 공개키
     * @param ownerPubkey 소유자 계정의 공개키
     * @param lamports 잔액 (lamports)
     * @param type 계정 유형
     * @param createAccountInTx 계정이 생성된 트랜잭션 시그니처
     * @param lastUpdatedAt 마지막 업데이트 시각
     * @param data 계정 데이터 (JSON 형식의 문자열)
     * @return CSV 형식의 문자열 한 줄
     * @throws JsonProcessingException JSON 직렬화 실패 시 발생
     */
    private String csv_accounts(
            String pubkey,
            String ownerPubkey,
            long lamports,
            String type,
            String createAccountInTx,
            OffsetDateTime lastUpdatedAt,
            String data // JSONB는 String으로 전달받는 것이 일반적입니다.
    ) throws JsonProcessingException {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("pubkey", pubkey);
        row.put("owner_pubkey", ownerPubkey);
        row.put("lamports", lamports);
        row.put("type", type);
        row.put("create_account_in_tx", createAccountInTx);
        row.put("last_updated_at", lastUpdatedAt != null ? lastUpdatedAt.toString() : null);
        row.put("data", data);

        // CsvSchemaProvider에 ACCOUNTS_SCHEMA가 정의되어 있다고 가정합니다.
        return csvMapper.writer(CsvSchemaProvider.ACCOUNTS_SCHEMA)
                .writeValueAsString(row);
    }
}
