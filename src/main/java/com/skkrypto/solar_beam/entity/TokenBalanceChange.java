package com.skkrypto.solar_beam.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "token_balance_changes", indexes = {
        @Index(name = "idx_token_balance_changes_tx_fk", columnList = "tx_primary_signature"),
        @Index(name = "idx_token_balance_changes_account", columnList = "account_pubkey"),
        @Index(name = "idx_token_balance_changes_mint", columnList = "mint_address"),
        @Index(name = "idx_token_balance_changes_owner", columnList = "owner_address")
})
@IdClass(TokenBalanceChangeId.class)
public class TokenBalanceChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mint_address", referencedColumnName = "pubkey", nullable = false)
    private Account mintAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_address", referencedColumnName = "pubkey")
    private Account ownerAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", referencedColumnName = "pubkey")
    private Account program;

    @Column(name = "pre_amount_raw", length = 40)
    private String preAmountRaw;

    @Column(name = "post_amount_raw", length = 40)
    private String postAmountRaw;

    @Column(name = "decimals")
    private Short decimals;

    @Column(name = "block_time", nullable = false)
    private OffsetDateTime blockTime;
}