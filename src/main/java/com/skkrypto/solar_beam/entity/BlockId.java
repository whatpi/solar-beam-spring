package com.skkrypto.solar_beam.entity;

import lombok.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BlockId implements Serializable {
    private Long slot;
    private OffsetDateTime blockTime;
}