package com.skkrypto.solar_beam.repository;

import com.skkrypto.solar_beam.entity.TransactionAccount;
import com.skkrypto.solar_beam.entity.TransactionAccountId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionAccountRepository extends JpaRepository<TransactionAccount, TransactionAccountId> {
}