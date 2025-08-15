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

    public UiTokenAmountDto getUiTokenAmount() {return uiTokenAmount;}
    public int getAccountIndex() {return accountIndex;}
    public String getMint() {return mint;}
    public String getOwner() {return owner;}
    public String getProgramId() {return programId;}
}
