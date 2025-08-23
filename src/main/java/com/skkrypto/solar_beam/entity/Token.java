package com.skkrypto.solar_beam.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tokens")
public class Token {

    @Id
    @Column(name = "mint_address", length = 44)
    private String mintAddress;

    @Column(name = "decimals", nullable = false)
    private Short decimals;

    @Column(name = "supply", precision = 38, scale = 0) // NUMERIC 타입의 정밀도와 스케일 지정
    private BigDecimal supply;

    @Column(name = "mint_authority", length = 44)
    private String mintAuthority;

    @Column(name = "freeze_authority", length = 44)
    private String freezeAuthority;

    @Column(name = "symbol", length = 20)
    private String symbol;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "logo_uri", columnDefinition = "TEXT")
    private String logoUri;

    @Column(name = "last_updated_at")
    private OffsetDateTime lastUpdatedAt;

}