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

    public long getComputeUnitsConsumed() {return computeUnitsConsumed;}
    public Object getErr() {return err;}
    public long getFee() {return fee;}
    public List<InnerInstructionDto> getInnerInstructions() {return innerInstructions;}
    public List<String> getLogMessages() {return logMessages;}
    public List<Long> getPostBalances() {return postBalances;}
    public List<TokenBalanceDto> getPostTokenBalances() {return postTokenBalances;}
    public List<Long> getPreBalances() {return preBalances;}
    public List<TokenBalanceDto> getPreTokenBalances() {return preTokenBalances;}
    public List<Object> getRewards() {return rewards;}
    public StatusDto getStatus() {return status;}

}
