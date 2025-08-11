package com.skkrypto.solar_beam.repository;

import com.skkrypto.solar_beam.entity.InstructionAccount;
import com.skkrypto.solar_beam.entity.InstructionAccountId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionAccountRepository extends JpaRepository<InstructionAccount, InstructionAccountId> {
}