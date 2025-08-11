package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnparsedInstructionDto extends InstructionDto {

    @JsonProperty("accounts")
    private List<String> accounts;

    @JsonProperty("data")
    private String data;

    // Getters and Setters
    public List<String> getAccounts() { return accounts; }
    public void setAccounts(List<String> accounts) { this.accounts = accounts; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}
