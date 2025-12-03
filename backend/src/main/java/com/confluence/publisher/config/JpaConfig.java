package com.confluence.publisher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class JpaConfig {
    
    @Value("${app.database-url}")
    private String databaseUrl;
    
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        // Ensure proper SQLite URL format
        // Handle both jdbc:sqlite: and jdbc:sqlite:/// formats
        String url = databaseUrl;
        if (!url.startsWith("jdbc:sqlite")) {
            url = "jdbc:sqlite:" + url;
        }
        // Convert jdbc:sqlite:/// to jdbc:sqlite: for local paths
        if (url.startsWith("jdbc:sqlite:///")) {
            url = "jdbc:sqlite:" + url.substring("jdbc:sqlite:///".length());
        }
        dataSource.setUrl(url);
        return dataSource;
    }
}

