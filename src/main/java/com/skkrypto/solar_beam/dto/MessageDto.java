package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDto {

    @JsonProperty("accountKeys")
    private List<AccountKeyDto> accountKeys;

    @JsonProperty("addressTableLookups")
    private List<AddressTableLookupDto> addressTableLookups;

    @JsonProperty("instructions")
    private List<InstructionDto> instructions;

    @JsonProperty("recentBlockhash")
    private String recentBlockhash;

    public List<AccountKeyDto> getAccountKeys() { return accountKeys; }
    public List<AddressTableLookupDto> getAddressTableLookups() { return addressTableLookups; }
    public List<InstructionDto> getInstructions() { return instructions; }
    public String getRecentBlockhash() { return recentBlockhash; }

}
