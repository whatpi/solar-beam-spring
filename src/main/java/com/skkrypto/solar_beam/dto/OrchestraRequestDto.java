package com.skkrypto.solar_beam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// JSON 변환을 위해 Getter와 기본 생성자가 필요합니다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrchestraRequestDto {
    private Long slot;
    private byte[] requestBody;
}
