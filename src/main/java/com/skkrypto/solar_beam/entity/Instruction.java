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
@Table(name = "instructions", indexes = {
        @Index(name = "idx_instructions_tx_fk", columnList = "tx_primary_signature"),
        @Index(name = "idx_instructions_program_id", columnList = "program_id")
})
public class Instruction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "tx_primary_signature", nullable = false, length = 88)
    private String txPrimarySignature;

    @Id
    @Column(name = "tx_block_time", nullable = false)
    private OffsetDateTime txBlockTime;

    @Column(name = "ix_path", nullable = false, columnDefinition = "TEXT")
    private String ixPath;

    // 1이면 최초 인스트럭션 2부터 inner
    @Column(name = "stack", nullable = false)
    private Short stack;

    @JoinColumn(name = "program_id")
    private String program;

    @Column(name = "raw_data", columnDefinition = "TEXT")
    private byte[] rawData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parsed_info", columnDefinition = "jsonb")
    private String parsedInfo;

    @Column(name = "parsed_type", length = 10)
    private String parsedType;
}