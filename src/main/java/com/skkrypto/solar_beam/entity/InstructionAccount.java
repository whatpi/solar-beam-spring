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
@Table(name = "instruction_accounts", indexes = {
        @Index(name = "idx_instruction_accounts_account_pubkey", columnList = "account_pubkey"),
        @Index(name = "idx_instruction_accounts_instruction_id", columnList = "instruction_id")
})
@IdClass(InstructionAccountId.class)
public class InstructionAccount {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instruction_id", referencedColumnName = "id")
    private Instruction instructionId;

    @Id
    @Column(name = "account_pubkey")
    private String accountPubkey;

    @Column(name = "instruction_tx_block_time", nullable = false)
    private OffsetDateTime instructionTxBlockTime;

    @Column(name = "account_index_in_instruction", nullable = false)
    private Short accountIndexInInstruction;
}