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
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transactions_block_slot", columnList = "block_slot")
})
@IdClass(TransactionId.class)
public class Transaction {

    @Id
    @Column(name = "primary_signature", length = 88)
    private String primarySignature;

    @Id
    @Column(name = "block_time")
    private OffsetDateTime blockTime;

    @Column(name = "idx_in_block", nullable = false)
    private Short idxInBlock;

    @Column(name = "block_slot", nullable = false)
    private Long blockSlot;

    @Column(name = "recent_blockhash", length = 44, nullable = false)
    private String recentBlockhash;

    @Column(name = "fee", nullable = false)
    private Long fee;

    @Column(name = "compute_units_consumed")
    private Long computeUnitsConsumed;

    @Column(name = "err_kind", length = 30)
    private String errKind;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "err_payload", columnDefinition = "jsonb")
    private String errPayload;

    @Column(name = "err_instruction_idx")
    private short errInstructionIdx;

    @Column(name = "err_instruction_custom")
    private short errInstructionCustom;


//    @Column(name = "error_message", columnDefinition = "TEXT")
//    private String errorMessage;

//    @Column(name = "version", length = 20)
//    private String version;

    @Type(ListArrayType.class)
    @Column(name = "log_messages", columnDefinition = "text[]")
    private List<String> logMessages;
}