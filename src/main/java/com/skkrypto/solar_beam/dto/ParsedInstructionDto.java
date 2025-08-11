package com.skkrypto.solar_beam.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParsedInstructionDto extends InstructionDto {

    @JsonProperty("parsed")
    private ParsedDataDto parsed;

    @JsonProperty("program")
    private String program;

    // Getters and Setters
    public ParsedDataDto getParsed() { return parsed; }
    public void setParsed(ParsedDataDto parsed) { this.parsed = parsed; }
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
}
