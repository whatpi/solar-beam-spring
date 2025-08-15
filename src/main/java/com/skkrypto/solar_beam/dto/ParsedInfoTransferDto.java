package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// transfer 전용이고 여기서 사용하지 않음

// rdb에서 일일히 검사하기 힘들어서 애플리케션 레벨에서 무결성 보장해야 (fk 관련)

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParsedInfoTransferDto {

    @JsonProperty("authority")
    private String authority;

    @JsonProperty("destination")
    private String destination;

    @JsonProperty("mint")
    private String mint;

    @JsonProperty("source")
    private String source;

    @JsonProperty("tokenAmount")
    private UiTokenAmountDto tokenAmount; // 기존 DTO 재사용

    // Getters and Setters
    // ... 모든 필드에 대한 Getter/Setter 추가 ...
}
