package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION) // JSON 필드를 보고 타입을 추론
@JsonSubTypes({
        @JsonSubTypes.Type(ParsedInstructionDto.class),
        @JsonSubTypes.Type(UnparsedInstructionDto.class)
})
public abstract class InstructionDto {

    @JsonProperty("programId")
    private String programId;

    @JsonProperty("stackHeight")
    private Integer stackHeight;

    // Getters and Setters
    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }
    public Integer getStackHeight() { return stackHeight; }
    public void setStackHeight(Integer stackHeight) { this.stackHeight = stackHeight; }
}