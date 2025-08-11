package com.skkrypto.solar_beam.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "instruction_accounts", indexes = {
        @Index(name = "idx_instruction_accounts_account_pubkey", columnList = "account_pubkey"),
        @Index(name = "idx_instruction_accounts_instruction_id", columnList = "instruction_id")
})
@IdClass(InstructionAccountId.class)
public class InstructionAccount {

    @Id
    @Column(name = "instruction_id")
    private Long instructionId;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_pubkey", referencedColumnName = "pubkey")
    private Account account;

    @Column(name = "instruction_tx_block_time", nullable = false)
    private OffsetDateTime instructionTxBlockTime;

    @Column(name = "account_index_in_instruction", nullable = false)
    private Short accountIndexInInstruction;
}