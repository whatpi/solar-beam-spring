package com.skkrypto.solar_beam.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@EqualsAndHashCode
public class BalanceChangeId implements Serializable {
    private Long id;
    private OffsetDateTime txBlockTime;
}