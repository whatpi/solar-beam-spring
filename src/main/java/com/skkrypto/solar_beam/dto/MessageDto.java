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

    // Getters and Setters
    // ... 모든 필드에 대한 Getter/Setter 추가 ...
}
