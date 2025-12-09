# Prompt 04: Backend Configuration Classes

## Role
You are an expert Java engineer.

## Task
Create Spring configuration classes for the Confluence Publisher application.

## Package
`com.confluence.publisher.config`

## Classes to Create

### 1. AppProperties
A `@ConfigurationProperties` class with prefix `app` containing:

| Property | Type | Default Value |
|----------|------|---------------|
| appName | String | "confluence-publisher" |
| databaseUrl | String | "jdbc:sqlite:./data/app.db" |
| attachmentDir | String | "storage/attachments" |
| confluenceUrl | String | "https://your-domain.atlassian.net" |
| confluenceUsername | String | "" |
| confluenceDefaultSpace | String | "DEV" |
| confluenceApiToken | String | "" |
| corsOrigins | List<String> | localhost:4200, 8080, 5173 |
| provider | String | "confluence-server" |
| schedulerIntervalSeconds | Integer | 5 |

Include a setter that can parse comma-separated CORS origins from environment variable.

### 2. WebConfig
Implements `WebMvcConfigurer` to configure CORS:
- Apply to `/api/**` paths
- Allow origins from AppProperties.corsOrigins
- Allow methods: GET, POST, PUT, DELETE, OPTIONS
- Allow all headers
- Allow credentials

### 3. JpaConfig
Creates a `DataSource` bean for SQLite:
- Use `DriverManagerDataSource` with SQLite JDBC driver
- Read URL from `app.database-url` property
- Handle both `jdbc:sqlite:` and `jdbc:sqlite:///` URL formats

### 4. DataInitializer
Implements `CommandLineRunner` to:
- Create the database directory if it doesn't exist
- Create the attachment directory if it doesn't exist
- Log initialization completion

### 5. Main Application Class
`ConfluencePublisherApplication` with:
- `@SpringBootApplication`
- `@EnableScheduling` for background job support
- `@EnableConfigurationProperties(AppProperties.class)`

## Configuration Properties Mapping

| Property | Environment Variable |
|----------|---------------------|
| app.database-url | APP_DATABASE_URL |
| app.attachment-dir | APP_ATTACHMENT_DIR |
| app.confluence-url | CONFLUENCE_URL |
| app.confluence-username | CONFLUENCE_USERNAME |
| app.confluence-api-token | CONFLUENCE_API_TOKEN |
| app.confluence-default-space | CONFLUENCE_DEFAULT_SPACE |
| app.provider | CONFLUENCE_PROVIDER |
| app.scheduler-interval-seconds | SCHEDULER_INTERVAL_SECONDS |
| app.cors-origins | CORS_ORIGINS |

## Verification Criteria
- Application starts and creates required directories
- CORS headers present in API responses
- Configuration properties load from application.yml and environment
- SQLite database file created on first run
