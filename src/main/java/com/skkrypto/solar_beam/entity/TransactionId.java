package com.skkrypto.solar_beam.entity;

import lombok.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TransactionId implements Serializable {
    private String primarySignature;
    private OffsetDateTime blockTime;
}