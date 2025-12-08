package com.confluence.publisher.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    
    private final AppProperties appProperties;
    
    @PostConstruct
    public void initializeDirectories() {
        try {
            // Ensure data directory exists for SQLite database
            String dbUrl = appProperties.getDatabaseUrl();
            String dbPath = dbUrl
                    .replace("jdbc:sqlite:", "")
                    .replace("jdbc:sqlite:///", "");
            
            // Handle both Windows and Unix paths
            String dbDir;
            if (dbPath.contains("/")) {
                dbDir = dbPath.substring(0, dbPath.lastIndexOf("/"));
            } else if (dbPath.contains("\\")) {
                dbDir = dbPath.substring(0, dbPath.lastIndexOf("\\"));
            } else {
                // If no path separator, database is in current directory
                dbDir = ".";
            }
            
            // Create directory (handles both relative and absolute paths)
            Path dbDirPath = Paths.get(dbDir);
            if (!Files.exists(dbDirPath)) {
                Files.createDirectories(dbDirPath);
                log.info("Created database directory: {}", dbDirPath.toAbsolutePath());
            }
            
            // Ensure attachment directory exists
            Path attachmentDir = Paths.get(appProperties.getAttachmentDir());
            if (!Files.exists(attachmentDir)) {
                Files.createDirectories(attachmentDir);
                log.info("Created attachment directory: {}", attachmentDir.toAbsolutePath());
            }
            
            log.info("Initialized data directories");
        } catch (Exception e) {
            log.error("Failed to initialize directories", e);
            throw new RuntimeException("Failed to initialize data directories", e);
        }
    }
}

