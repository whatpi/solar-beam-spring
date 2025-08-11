package com.skkrypto.solar_beam.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "transaction_accounts", indexes = {
        @Index(name = "idx_transaction_accounts_pubkey", columnList = "account_pubkey"),
        @Index(name = "idx_transaction_accounts_signature", columnList = "signature")
})
@IdClass(TransactionAccountId.class)
public class TransactionAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Id
    @Column(name = "tx_block_time")
    private OffsetDateTime txBlockTime;

    @Column(name = "tx_primary_signature", length = 88, nullable = false)
    private String txPrimarySignature;

    @Column(name = "account_index_in_tx", nullable = false)
    private Short accountIndexInTx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_pubkey", referencedColumnName = "pubkey", nullable = false)
    private Account account;

    @Column(name = "signature", length = 88)
    private String signature;

    @Column(name = "is_writable", nullable = false)
    private Boolean isWritable;

    @Column(name = "source_of_account", length = 20, nullable = false)
    private String sourceOfAccount;
}