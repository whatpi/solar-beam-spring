package com.skkrypto.solar_beam.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto {

    @JsonProperty("message")
    private MessageDto message;

    @JsonProperty("signatures")
    private List<String> signatures;

    // Getters and Setters
    public MessageDto getMessage() { return message; }
    public void setMessage(MessageDto message) { this.message = message; }
    public List<String> getSignatures() { return signatures; }
    public void setSignatures(List<String> signatures) { this.signatures = signatures; }
}
