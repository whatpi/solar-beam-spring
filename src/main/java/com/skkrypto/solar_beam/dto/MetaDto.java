package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaDto {

    @JsonProperty("computeUnitsConsumed")
    private long computeUnitsConsumed;

    @JsonProperty("err")
    private Object err; // null 또는 에러 객체가 올 수 있으므로 Object 타입으로 선언

    @JsonProperty("fee")
    private long fee;

    @JsonProperty("innerInstructions")
    private List<InnerInstructionDto> innerInstructions; // 내용이 비어있어 Object로 처리

    @JsonProperty("logMessages")
    private List<String> logMessages;

    @JsonProperty("postBalances")
    private List<Long> postBalances;

    @JsonProperty("postTokenBalances")
    private List<TokenBalanceDto> postTokenBalances;

    @JsonProperty("preBalances")
    private List<Long> preBalances;

    @JsonProperty("preTokenBalances")
    private List<TokenBalanceDto> preTokenBalances;

    @JsonProperty("rewards")
    private List<Object> rewards; // 내용이 비어있어 Object로 처리

    @JsonProperty("status")
    private StatusDto status;

    // Getters and Setters
    // ... 모든 필드에 대한 Getter/Setter 추가 ...
}
