package com.confluence.publisher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class JpaConfig {
    
    @Value("${app.database-url}")
    private String databaseUrl;
    
    @Bean
    public DataSource dataSource() {
        // Ensure database directory exists before creating DataSource
        ensureDatabaseDirectoryExists();
        
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        // Ensure proper SQLite URL format
        // Handle both jdbc:sqlite: and jdbc:sqlite:/// formats
        String url = databaseUrl;
        if (!url.startsWith("jdbc:sqlite")) {
            url = "jdbc:sqlite:" + url;
        }
        // Fix for SQLite on Windows: standardizing file path handling
        // If it starts with jdbc:sqlite:./ or jdbc:sqlite:.\ (relative path), keep it as is
        // but ensure backslashes are handled if needed, though SQLite JDBC handles forward slashes fine on Windows.
        // The issue is likely the mix of absolute/relative path interpretation.
        
        dataSource.setUrl(url);
        return dataSource;
    }
    
    private void ensureDatabaseDirectoryExists() {
        try {
            // Strip the prefix to get the raw path
            String rawPath = databaseUrl;
            if (rawPath.startsWith("jdbc:sqlite:")) {
                rawPath = rawPath.substring("jdbc:sqlite:".length());
            }
            
            // Handle file URL schema if present
            if (rawPath.startsWith("//")) {
                // This usually implies an absolute path in some conventions, but for SQLite JDBC
                // jdbc:sqlite://dir/file.db usually maps to /dir/file.db
                // jdbc:sqlite:./dir/file.db maps to relative
            }
            
            // Simple cleanup for the purpose of finding the directory
            String dbPath = rawPath;
            
            // Extract directory path
            String dbDir;
            if (dbPath.contains("/")) {
                dbDir = dbPath.substring(0, dbPath.lastIndexOf("/"));
            } else if (dbPath.contains("\\")) {
                dbDir = dbPath.substring(0, dbPath.lastIndexOf("\\"));
            } else {
                // Database in current directory or no path separators
                return;
            }
            
            // Create directory if it doesn't exist
            if (!dbDir.isEmpty()) {
                Path dbDirPath = Paths.get(dbDir);
                if (!Files.exists(dbDirPath)) {
                    Files.createDirectories(dbDirPath);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create database directory", e);
        }
    }
}

