package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public record SolanaBlockNotification(
            @JsonProperty("network") String network,
            @JsonProperty("notificationId") String notificationId,
            @JsonProperty("slot") long slot,
            @JsonProperty("version") String version,
            @JsonProperty("blockhash") String blockhash,
            @JsonProperty("parentSlot") long parentSlot,
            @JsonProperty("parentBlockhash") String parentBlockhash,
            @JsonProperty("timestamp") long timestamp
) {}
