package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RewardDto {

    @JsonProperty("commission")
    private Integer commission; // null일 수 있으므로 Wrapper 타입 사용

    @JsonProperty("lamports")
    private long lamports;

    @JsonProperty("postBalance")
    private long postBalance;

    @JsonProperty("pubkey")
    private String pubkey;

    @JsonProperty("rewardType")
    private String rewardType;

    public Integer getCommission() {return commission;}
    public long getLamports() {return lamports;}
    public long getPostBalance() {return postBalance;}
    public String getPubkey() {return pubkey;}
    public String getRewardType() {return rewardType;}

    // Getters and Setters
    // ... 모든 필드에 대한 Getter/Setter 추가 ...
}