package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParsedInfoDto {

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
