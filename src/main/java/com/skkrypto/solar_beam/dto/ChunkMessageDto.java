package com.skkrypto.solar_beam.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.skkrypto.solar_beam.dto.BlockMetadataDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 자동으로 만들어줍니다.
public class ChunkMessageDto {

    private BlockMetadataDto metadata;
    private List<JsonNode> transactions;
}