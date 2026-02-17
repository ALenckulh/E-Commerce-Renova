package com.retificarenova.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

/**
 * Configuration for Flyway database migrations.
 * Handles database schema initialization and migration.
 */
@Configuration
public class FlywayConfig {
    // Flyway configuration is auto-configured by Spring Boot
    // This class is kept for future custom configuration if needed
}
