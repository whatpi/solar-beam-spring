package com.skkrypto.solar_beam.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "instructions", indexes = {
        @Index(name = "idx_instructions_tx_fk", columnList = "tx_primary_signature"),
        @Index(name = "idx_instructions_program_id", columnList = "program_id")
})
public class Instruction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tx_primary_signature", nullable = false, length = 88)
    private String txPrimarySignature;

    @Column(name = "tx_block_time", nullable = false)
    private OffsetDateTime txBlockTime;

    @Column(name = "ix_path", nullable = false, columnDefinition = "TEXT")
    private String ixPath;

    @Column(name = "depth", nullable = false)
    private Short depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Account program;

    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;

    @Column(name = "parsed_info_amount")
    private Integer parsedInfoAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parsed_info_authroity")
    private Account authority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parsed_info_destination")
    private Account destination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parsed_info_source")
    private Account source;

    @Column(name = "parsed_type", length = 10)
    private String parsedType;
}