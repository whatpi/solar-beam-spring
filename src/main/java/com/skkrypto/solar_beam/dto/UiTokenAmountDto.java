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

    public String getAmount() {return amount;}
    public void setAmount(String amount) {this.amount = amount;}
    public int getDecimals() {return decimals;}
    public void setDecimals(int decimals) {this.decimals = decimals;}
    public Double getUiAmount() {return uiAmount;}
    public void setUiAmount(Double uiAmount) {this.uiAmount = uiAmount;}
    public String getUiAmountString() {return uiAmountString;}
    public void setUiAmountString(String uiAmountString) {this.uiAmountString = uiAmountString;}

}