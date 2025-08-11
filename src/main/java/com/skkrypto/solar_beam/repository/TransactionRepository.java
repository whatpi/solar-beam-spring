package com.skkrypto.solar_beam.repository;

import com.skkrypto.solar_beam.entity.Transaction;
import com.skkrypto.solar_beam.entity.TransactionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, TransactionId> {
}