# Prompt 04: Backend Configuration Classes

## Context
Continue building the Confluence Publisher application. Create the Spring configuration classes for application properties, CORS, JPA, and data initialization.

## Requirements

### Package Structure
All configuration classes should be in `com.confluence.publisher.config` package.

### Configuration: AppProperties
Create `config/AppProperties.java`:
```java
package com.confluence.publisher.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    
    private String appName = "confluence-publisher";
    private String databaseUrl = "jdbc:sqlite:./data/app.db";
    private String attachmentDir = "storage/attachments";
    private String confluenceUrl = "https://your-domain.atlassian.net";
    private String confluenceUsername = "";
    private String confluenceDefaultSpace = "DEV";
    private String confluenceApiToken = "";
    private List<String> corsOrigins = List.of("http://localhost:5173", "http://localhost:4200", "http://localhost:8080");
    private String provider = "confluence-server";
    private Integer schedulerIntervalSeconds = 5;
    
    // Helper method to parse comma-separated CORS origins from environment
    public void setCorsOrigins(String corsOriginsString) {
        if (corsOriginsString != null && !corsOriginsString.isEmpty()) {
            this.corsOrigins = List.of(corsOriginsString.split(","));
        }
    }
}
```

### Configuration: WebConfig (CORS)
Create `config/WebConfig.java`:
```java
package com.confluence.publisher.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final AppProperties appProperties;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(appProperties.getCorsOrigins().toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### Configuration: JpaConfig (SQLite DataSource)
Create `config/JpaConfig.java`:
```java
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
```

### Configuration: DataInitializer
Create `config/DataInitializer.java`:
```java
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
        
        // Ensure attachment directory exists
        Files.createDirectories(Paths.get(appProperties.getAttachmentDir()));
        
        log.info("Initialized data directories");
    }
}
```

### Main Application Class
Create/Update `ConfluencePublisherApplication.java`:
```java
package com.confluence.publisher;

import com.confluence.publisher.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(AppProperties.class)
public class ConfluencePublisherApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfluencePublisherApplication.class, args);
    }
}
```

## Key Design Decisions
1. **@ConfigurationProperties**: Type-safe configuration binding with `app.*` prefix
2. **CORS configuration**: Configurable origins via environment variables, supports multiple origins
3. **SQLite URL handling**: JpaConfig handles different URL formats for local dev and Docker
4. **DataInitializer**: Creates required directories on startup (data/, storage/attachments/)
5. **@EnableScheduling**: Enables Spring's scheduled task execution for the page scheduler
6. **Environment variable support**: All sensitive config can be overridden via environment variables

## Configuration Properties Mapping
| Property | Environment Variable | Default |
|----------|---------------------|---------|
| `app.database-url` | `APP_DATABASE_URL` | `jdbc:sqlite:./data/app.db` |
| `app.attachment-dir` | `APP_ATTACHMENT_DIR` | `storage/attachments` |
| `app.confluence-url` | `CONFLUENCE_URL` | `https://your-domain.atlassian.net` |
| `app.confluence-username` | `CONFLUENCE_USERNAME` | (empty) |
| `app.confluence-api-token` | `CONFLUENCE_API_TOKEN` | (empty) |
| `app.confluence-default-space` | `CONFLUENCE_DEFAULT_SPACE` | `DEV` |
| `app.provider` | `CONFLUENCE_PROVIDER` | `confluence-server` |
| `app.scheduler-interval-seconds` | `SCHEDULER_INTERVAL_SECONDS` | `5` |
| `app.cors-origins` | `CORS_ORIGINS` | `http://localhost:4200,...` |

## Verification
- Application starts and creates `data/` and `storage/attachments/` directories
- CORS headers are present in API responses
- Configuration properties are properly loaded
- SQLite database file is created on first run
