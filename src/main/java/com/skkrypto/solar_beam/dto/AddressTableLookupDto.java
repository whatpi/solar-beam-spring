package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressTableLookupDto {

    @JsonProperty("accountKey")
    private String accountKey;

    @JsonProperty("readonlyIndexes")
    private List<Integer> readonlyIndexes;

    @JsonProperty("writableIndexes")
    private List<Integer> writableIndexes;

    // Getters and Setters
    // ... 모든 필드에 대한 Getter/Setter 추가 ...
}
