package com.skkrypto.solar_beam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlockMetadataDto {

    private Long slot;
    private java.time.OffsetDateTime blockTime;
    // 필요한 다른 메타데이터가 있다면 여기에 추가 (예: String blockHash)
}