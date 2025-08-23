package com.skkrypto.solar_beam.entity;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(RewardsId.class)
public class Rewards {

    @Id
    @Column(name = "block_slot", nullable = false)
    private Long blockSlot;

    @Column(name = "idx_in_block", nullable = false)
    private short idx_in_block;

    @Id
    @Column(name = "block_time", nullable = false)
    private OffsetDateTime blockTime;

    @Column(name = "pubkey", nullable = false)
    private String pubkey;

    @Column(name = "lamports", nullable = false)
    private Long lamports;

    @Column(name = "post_balance", nullable = false)
    private Long postBalance;

    @Column(name = "reward_type", nullable = false)
    private String rewardType;

    @Column(name = "commission")
    private short commission;
}
