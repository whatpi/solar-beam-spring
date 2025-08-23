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
@Table(name = "transaction_accounts", indexes = {
        @Index(name = "idx_transaction_accounts_pubkey", columnList = "account_pubkey")
})
@IdClass(TransactionAccountId.class)
public class TransactionAccount {

    @Id
    @Column(name = "tx_primary_signature", length = 88, nullable = false)
    private String txPrimarySignature;

    @Id
    @Column(name = "account_index_in_tx", nullable = false)
    private Short accountIndexInTx;

    @Id
    @Column(name = "tx_block_time")
    private OffsetDateTime txBlockTime;

    @Column(name = "account_pubkey", length = 44, nullable = false)
    private String account;

    @Column(name = "signature", length = 88)
    private String signature;

    @Column(name = "is_writable", nullable = false)
    private Boolean isWritable;

    @Column(name = "source_of_account", length = 20, nullable = false)
    private String sourceOfAccount;
}