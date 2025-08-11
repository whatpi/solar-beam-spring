package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParsedDataDto {

    @JsonProperty("info")
    private ParsedInfoDto info;

    @JsonProperty("type")
    private String type;

    // Getters and Setters
    public ParsedInfoDto getInfo() { return info; }
    public void setInfo(ParsedInfoDto info) { this.info = info; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
