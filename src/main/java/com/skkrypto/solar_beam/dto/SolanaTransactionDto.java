package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// JSON에 정의되지 않은 필드가 있어도 오류 없이 파싱하기 위해 설정
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolanaTransactionDto {

    @JsonProperty("meta")
    private MetaDto meta;

    @JsonProperty("transaction")
    private TransactionDto transaction;

//    @JsonProperty("version")
//    private int version;

    // Getters and Setters
    public MetaDto getMeta() { return meta; }
    public void setMeta(MetaDto meta) { this.meta = meta; }
    public TransactionDto getTransaction() { return transaction; }
    public void setTransaction(TransactionDto transaction) { this.transaction = transaction; }
//    public int getVersion() { return version; }
//    public void setVersion(int version) { this.version = version; }
}