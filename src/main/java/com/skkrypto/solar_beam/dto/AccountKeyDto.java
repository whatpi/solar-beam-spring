package com.skkrypto.solar_beam.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountKeyDto {

    @JsonProperty("pubkey")
    private String pubkey;

    @JsonProperty("signer")
    private boolean signer;

    @JsonProperty("source")
    private String source;

    @JsonProperty("writable")
    private boolean writable;

    public String getPubkey() { return pubkey; }

    public boolean isSigner() { return signer; }

    public String getSource() { return source; }

    public boolean isWritable() { return writable; }
}
