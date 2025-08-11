package com.skkrypto.solar_beam.repository;

import com.skkrypto.solar_beam.entity.Instruction;
import com.skkrypto.solar_beam.entity.InstructionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction, InstructionId> {
}