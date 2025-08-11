package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UiTokenAmountDto {

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("decimals")
    private int decimals;

    @JsonProperty("uiAmount")
    private Double uiAmount; // null이 될 수 있으므로 primitive type 대신 Wrapper class 사용

    @JsonProperty("uiAmountString")
    private String uiAmountString;

    // Getters and Setters
    // ... 모든 필드에 대한 Getter/Setter 추가 ...
}