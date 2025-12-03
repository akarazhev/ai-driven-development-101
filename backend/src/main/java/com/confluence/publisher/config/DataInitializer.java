package com.confluence.publisher.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final AppProperties appProperties;
    
    @Override
    public void run(String... args) throws Exception {
        // Ensure data directory exists
        String dbPath = appProperties.getDatabaseUrl()
                .replace("jdbc:sqlite:", "")
                .replace("jdbc:sqlite:///", "");
        if (dbPath.contains("/")) {
            String dbDir = dbPath.substring(0, dbPath.lastIndexOf("/"));
            Files.createDirectories(Paths.get(dbDir));
        }
        
        // Ensure media directory exists
        Files.createDirectories(Paths.get(appProperties.getMediaDir()));
        
        log.info("Initialized data directories");
    }
}

