package com.skkrypto.solar_beam.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "balance_changes", indexes = {
        @Index(name = "idx_balance_changes_transactions_tx_primary_signature", columnList = "tx_primary_signature"),
        @Index(name = "idx_balance_changes_accounts_account_pubkey", columnList = "account_pubkey")
})
@IdClass(BalanceChangeId.class)
public class BalanceChange {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Id
    @Column(name = "tx_block_time")
    private OffsetDateTime txBlockTime;

    @Column(name = "tx_primary_signature", length = 88, nullable = false)
    private String txPrimarySignature;

    @Column(name = "idx_in_tx", nullable = false)
    private Short idxInTx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_pubkey", referencedColumnName = "pubkey", nullable = false)
    private Account account;

    @Column(name = "pre_balance", nullable = false)
    private Long preBalance;

    @Column(name = "post_balance", nullable = false)
    private Long postBalance;

    @Column(name = "block_time", nullable = false)
    private OffsetDateTime blockTime;
}