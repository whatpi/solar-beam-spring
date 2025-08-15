package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusDto {

    @JsonProperty("Ok") // JSON 키가 대문자로 시작하므로 명시
    private Object ok;

    public Object getOk() { return ok; }
}
