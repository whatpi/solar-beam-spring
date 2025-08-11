package com.skkrypto.solar_beam.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountKeyDto {

    @JsonProperty("pubkey")
    private String pubkey;

    @JsonProperty("signer")
    private boolean signer;

    @JsonProperty("source")
    private String source;

    @JsonProperty("writable")
    private boolean writable;

    // Getters and Setters
    // ... 모든 필드에 대한 Getter/Setter 추가 ...
}
