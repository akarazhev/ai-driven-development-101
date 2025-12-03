package com.socialmedia.automation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    
    private String appName = "social-app";
    private String databaseUrl = "jdbc:sqlite:./data/app.db";
    private String mediaDir = "storage/media";
    private List<String> corsOrigins = List.of("http://localhost:5173", "http://localhost:4200", "http://localhost:8080");
    private String provider = "stub";
    private Integer schedulerIntervalSeconds = 5;
    
    // Helper method to parse comma-separated CORS origins from environment
    public void setCorsOrigins(String corsOriginsString) {
        if (corsOriginsString != null && !corsOriginsString.isEmpty()) {
            this.corsOrigins = List.of(corsOriginsString.split(","));
        }
    }
}

