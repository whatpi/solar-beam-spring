package com.skkrypto.solar_beam.entity;

import lombok.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RewardsId implements Serializable {
    private Long blockSlot;
    private short idx_in_block;
    private OffsetDateTime BlockTime;
}
