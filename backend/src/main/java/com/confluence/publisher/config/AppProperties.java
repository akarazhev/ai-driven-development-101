package com.confluence.publisher.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
    
    private String appName = "confluence-publisher";
    private String databaseUrl = "jdbc:sqlite:./data/app.db";
    private String attachmentDir = "storage/attachments";
    private String confluenceUrl = "https://your-domain.atlassian.net";
    private String confluenceUsername = "";
    private String confluenceDefaultSpace = "DEV";
    private String confluenceApiToken = "";
    private List<String> corsOrigins = List.of("http://localhost:5173", "http://localhost:4200", "http://localhost:8080");
    private String provider = "confluence-stub";
    private Integer schedulerIntervalSeconds = 5;
}

