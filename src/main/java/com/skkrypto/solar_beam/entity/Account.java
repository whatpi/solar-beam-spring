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
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "pubkey", length = 44)
    private String pubkey;

    @Column(name = "owner_pubkey", length = 44)
    private String owner;

    @Column(name = "lamports")
    private Long lamports;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "mint_address", length = 44)
    private String mintAddress;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "create_account_in_tx", length = 88)
    private String createAccountInTx;

    @Column(name = "first_seen_at")
    private OffsetDateTime firstSeenAt;

    @Column(name = "last_updated_at")
    private OffsetDateTime lastUpdatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "jsonb")
    private String data;

}
