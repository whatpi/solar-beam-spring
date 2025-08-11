package com.skkrypto.solar_beam.entity;

import lombok.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InstructionId implements Serializable {
    private Long id;
    private OffsetDateTime txBlockTime;
}