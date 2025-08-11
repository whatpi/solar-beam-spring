package com.skkrypto.solar_beam.repository;

import com.skkrypto.solar_beam.entity.BalanceChange;
import com.skkrypto.solar_beam.entity.BalanceChangeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceChangeRepository extends JpaRepository<BalanceChange, BalanceChangeId> {
}