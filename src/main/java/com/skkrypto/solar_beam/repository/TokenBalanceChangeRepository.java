package com.skkrypto.solar_beam.repository;

import com.skkrypto.solar_beam.entity.TokenBalanceChange;
import com.skkrypto.solar_beam.entity.TokenBalanceChangeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBalanceChangeRepository extends JpaRepository<TokenBalanceChange, TokenBalanceChangeId> {
}