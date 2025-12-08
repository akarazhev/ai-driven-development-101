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
        try {
            // Try loading from project root (parent of backend)
            loadDotenv("../");
            // Try loading from current directory
            loadDotenv("./");
        } catch (Exception e) {
            // Ignore if .env cannot be loaded
        }
    }

    private static void loadDotenv(String path) {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(path)
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                // Only set if not already present in OS environment (Env vars take precedence over .env)
                if (System.getenv(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });
        } catch (Exception e) {
            // Directory might not exist or other error, safe to ignore
        }
    }
}

