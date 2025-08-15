package com.skkrypto.solar_beam.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {

    @Bean
    public FlywayMigrationStrategy repairStrategy() {
        return flyway -> {
            // repair를 먼저 실행하고, 그 다음 migrate를 실행합니다.
            flyway.repair();
            flyway.migrate();
        };
    }
}