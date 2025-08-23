package com.skkrypto.solar_beam.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "blocks", indexes = {
        @Index(name = "idx_blocks_previous_blockhash", columnList = "previous_blockhash"),
//        @Index(name = "idx_blocks_block_height", columnList = "block_height"),
})
@IdClass(BlockId.class)
public class Block {

    @Id
    @Column(name = "slot")
    private Long slot;

    @Id
    @Column(name = "block_time")
    private OffsetDateTime blockTime;

    @Column(name = "block_height", nullable = false)
    private Long blockHeight;

    @Column(name = "block_hash", length = 44, nullable = false)
    private String blockHash;

    @Column(name = "parent_slot", nullable = false)
    private Long parentSlot;

    @Column(name = "previous_blockhash", length = 44, nullable = false)
    private String previousBlockhash;

    @Column(name = "backend_status", length = 20, nullable = false)
    private String bakendStatus = "PROCESSING";

    @Column(name = "total_chunks")
    private Short totalChunks;

    @Column(name = "process_chunks")
    private Short processChunks = 0;

    public BlockId getBlockId() {
        return new BlockId(slot, blockTime);
    }
}