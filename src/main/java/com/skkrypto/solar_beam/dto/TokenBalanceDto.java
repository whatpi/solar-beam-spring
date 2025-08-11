package com.skkrypto.solar_beam.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenBalanceDto {

    @JsonProperty("accountIndex")
    private int accountIndex;

    @JsonProperty("mint")
    private String mint;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("programId")
    private String programId;

    @JsonProperty("uiTokenAmount")
    private UiTokenAmountDto uiTokenAmount;

    // Getters and Setters
    // ... 모든 필드에 대한 Getter/Setter 추가 ...
}
