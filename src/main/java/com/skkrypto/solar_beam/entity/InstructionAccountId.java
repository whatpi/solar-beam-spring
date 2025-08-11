package com.skkrypto.solar_beam.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class InstructionAccountId implements Serializable {
    private Long instructionId;
    private String account; // 필드명은 참조하는 엔티티의 필드명과 일치해야 함
}