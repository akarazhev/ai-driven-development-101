package com.confluence.publisher;

import com.confluence.publisher.config.AppProperties;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(AppProperties.class)
public class ConfluencePublisherApplication {

    public static void main(String[] args) {
        loadEnv();
        SpringApplication.run(ConfluencePublisherApplication.class, args);
    }

    private static void loadEnv() {
        System.out.println("Current Working Directory: " + System.getProperty("user.dir"));
        
        // Strategy: Look in common locations
        // 1. Current directory
        boolean loaded = loadDotenv("./");
        
        // 2. Parent directory (likely project root if running from backend/)
        if (!loaded) {
            loaded = loadDotenv("../");
        }
        
        // 3. Specific check for when running inside backend subdir but .env is in root
        if (!loaded) {
            try {
                java.nio.file.Path currentPath = java.nio.file.Paths.get("").toAbsolutePath();
                if (currentPath.endsWith("backend")) {
                     loaded = loadDotenv(currentPath.getParent().toString());
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private static boolean loadDotenv(String path) {
        try {
            java.io.File envFile = new java.io.File(path, ".env");
            if (!envFile.exists()) {
                System.out.println("No .env found at: " + envFile.getAbsolutePath());
                return false;
            }
            
            System.out.println("Found .env at: " + envFile.getAbsolutePath());

            Dotenv dotenv = Dotenv.configure()
                    .directory(path)
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                // Only set if not already present in OS environment (Env vars take precedence over .env)
                if (System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                    // Log loaded variables (masking sensitive values)
                    if (entry.getKey().contains("TOKEN") || entry.getKey().contains("PASSWORD")) {
                        System.out.println("Loaded " + entry.getKey() + " from .env: ******");
                    } else {
                        System.out.println("Loaded " + entry.getKey() + " from .env: " + entry.getValue());
                    }
                }
            });
            return true;
        } catch (Exception e) {
            System.out.println("Error loading .env from " + path + ": " + e.getMessage());
            return false;
        }
    }
}

