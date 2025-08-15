package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode; // JsonNode import 추가

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParsedDataDto {

    @JsonProperty("info")
    private JsonNode info; // 타입을 JsonNode로 변경

    @JsonProperty("type")
    private String type;

    // Getters and Setters
    public JsonNode getInfo() { return info; }
    public void setInfo(JsonNode info) { this.info = info; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}