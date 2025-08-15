package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockDto {

    @JsonProperty("blockHeight")
    private long blockHeight;

    @JsonProperty("blockTime")
    private long blockTime;

    @JsonProperty("blockhash")
    private String blockhash;

    @JsonProperty("parentSlot")
    private long parentSlot;

    @JsonProperty("previousBlockhash")
    private String previousBlockhash;

    @JsonProperty("rewards")
    private List<RewardDto> rewards;

    @JsonProperty("transactions")
    private List<SolanaTransactionDto> transactions; // 기존에 만든 DTO 재사용

}
