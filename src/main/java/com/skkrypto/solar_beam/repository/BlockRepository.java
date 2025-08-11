package com.skkrypto.solar_beam.repository;

import com.skkrypto.solar_beam.entity.Block;
import com.skkrypto.solar_beam.entity.BlockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends JpaRepository<Block, BlockId> {
}