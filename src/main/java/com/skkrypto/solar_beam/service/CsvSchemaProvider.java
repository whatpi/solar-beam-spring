package com.skkrypto.solar_beam.service;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public final class CsvSchemaProvider {

    /**
     * transactions 테이블용 스키마
     */
    public static final CsvSchema TRANSACTIONS_SCHEMA = CsvSchema.builder()
            .addColumn("primary_signature", CsvSchema.ColumnType.STRING)
            .addColumn("idx_in_block", CsvSchema.ColumnType.NUMBER)
            .addColumn("block_slot", CsvSchema.ColumnType.NUMBER)
            .addColumn("block_time", CsvSchema.ColumnType.STRING) // Timestamps are sent as strings
            .addColumn("recent_blockhash", CsvSchema.ColumnType.STRING)
            .addColumn("fee", CsvSchema.ColumnType.NUMBER)
            .addColumn("compute_units_consumed", CsvSchema.ColumnType.NUMBER)
            .addColumn("log_messages", CsvSchema.ColumnType.STRING) // Array needs special formatting, e.g., "{\"log1\",\"log2\"}"
            .build()
            .withoutHeader();

    /**
     * transaction_accounts 테이블용 스키마
     */
    public static final CsvSchema TRANSACTION_ACCOUNTS_SCHEMA = CsvSchema.builder()
            .addColumn("tx_primary_signature", CsvSchema.ColumnType.STRING)
            .addColumn("tx_block_time", CsvSchema.ColumnType.STRING)
            .addColumn("account_index_in_tx", CsvSchema.ColumnType.NUMBER)
            .addColumn("account_pubkey", CsvSchema.ColumnType.STRING)
            .addColumn("signature", CsvSchema.ColumnType.STRING)
            .addColumn("is_writable", CsvSchema.ColumnType.BOOLEAN)
            .addColumn("source_of_account", CsvSchema.ColumnType.STRING)
            .build()
            .withoutHeader();

    /**
     * instructions 테이블용 스키마
     */
    public static final CsvSchema INSTRUCTIONS_SCHEMA = CsvSchema.builder()
            .addColumn("id", CsvSchema.ColumnType.NUMBER)
            .addColumn("tx_primary_signature", CsvSchema.ColumnType.STRING)
            .addColumn("tx_block_time", CsvSchema.ColumnType.STRING)
            .addColumn("ix_path", CsvSchema.ColumnType.STRING)
            .addColumn("depth", CsvSchema.ColumnType.NUMBER)
            .addColumn("program_id", CsvSchema.ColumnType.STRING)
            .addColumn("raw_data", CsvSchema.ColumnType.STRING)
            .addColumn("parsed_info", CsvSchema.ColumnType.STRING) // JSON object serialized to a string
            .addColumn("parsed_type", CsvSchema.ColumnType.STRING)
            .build()
            .withoutHeader();

    /**
     * instruction_accounts 테이블용 스키마
     */
    public static final CsvSchema INSTRUCTION_ACCOUNTS_SCHEMA = CsvSchema.builder()
            .addColumn("instruction_accounts_id", CsvSchema.ColumnType.NUMBER)
            .addColumn("instruction_id", CsvSchema.ColumnType.NUMBER)
            .addColumn("instruction_tx_block_time", CsvSchema.ColumnType.STRING)
            .addColumn("account_pubkey", CsvSchema.ColumnType.STRING)
            .addColumn("account_index_in_instruction", CsvSchema.ColumnType.NUMBER)
            .build()
            .withoutHeader();

    /**
     * balance_changes 테이블용 스키마
     */
    public static final CsvSchema BALANCE_CHANGES_SCHEMA = CsvSchema.builder()
            .addColumn("id", CsvSchema.ColumnType.NUMBER)
            .addColumn("tx_primary_signature", CsvSchema.ColumnType.STRING)
            .addColumn("tx_block_time", CsvSchema.ColumnType.STRING)
            .addColumn("idx_in_tx", CsvSchema.ColumnType.NUMBER)
            .addColumn("account_pubkey", CsvSchema.ColumnType.STRING)
            .addColumn("pre_balance", CsvSchema.ColumnType.NUMBER)
            .addColumn("post_balance", CsvSchema.ColumnType.NUMBER)
            .addColumn("block_time", CsvSchema.ColumnType.STRING)
            .build()
            .withoutHeader();

    /**
     * token_balance_changes 테이블용 스키마
     */
    public static final CsvSchema TOKEN_BALANCE_CHANGES_SCHEMA = CsvSchema.builder()
            .addColumn("id", CsvSchema.ColumnType.NUMBER)
            .addColumn("tx_primary_signature", CsvSchema.ColumnType.STRING)
            .addColumn("tx_block_time", CsvSchema.ColumnType.STRING)
            .addColumn("idx_in_tx", CsvSchema.ColumnType.NUMBER)
            .addColumn("account_pubkey", CsvSchema.ColumnType.STRING)
            .addColumn("mint_address", CsvSchema.ColumnType.STRING)
            .addColumn("owner_address", CsvSchema.ColumnType.STRING)
            .addColumn("program_id", CsvSchema.ColumnType.STRING)
            .addColumn("pre_amount_raw", CsvSchema.ColumnType.STRING)
            .addColumn("post_amount_raw", CsvSchema.ColumnType.STRING)
            .addColumn("decimals", CsvSchema.ColumnType.NUMBER)
            .addColumn("block_time", CsvSchema.ColumnType.STRING)
            .build()
            .withoutHeader();

    // Private constructor to prevent instantiation
    private CsvSchemaProvider() {}
}