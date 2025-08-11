package com.skkrypto.solar_beam.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "pubkey", length = 44)
    private String pubkey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_pubkey")
    private Account owner;

    @Column(name = "lamports")
    private Long lamports;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "create_account_in_tx", length = 88)
    private String createAccountInTx;

    @Column(name = "last_updated_at")
    private OffsetDateTime lastUpdatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "jsonb")
    private String data;
}