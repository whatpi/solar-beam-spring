package com.skkrypto.solar_beam.repository;

import com.skkrypto.solar_beam.entity.Rewards;
import com.skkrypto.solar_beam.entity.RewardsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<Rewards, RewardsId> {
}
