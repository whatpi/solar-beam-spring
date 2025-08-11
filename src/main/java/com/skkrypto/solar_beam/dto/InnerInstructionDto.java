package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InnerInstructionDto {

    @JsonProperty("index")
    private int index;

    @JsonProperty("instructions")
    private List<InstructionDto> instructions;

    // Getters and Setters
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    public List<InstructionDto> getInstructions() { return instructions; }
    public void setInstructions(List<InstructionDto> instructions) { this.instructions = instructions; }
}