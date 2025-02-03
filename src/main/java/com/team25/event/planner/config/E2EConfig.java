package com.team25.event.planner.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("e2e")
@RequiredArgsConstructor
public class E2EConfig {
    private final Flyway flyway;

    @PostConstruct
    public void resetDatabase() {
        flyway.clean();
        flyway.migrate();
    }
}