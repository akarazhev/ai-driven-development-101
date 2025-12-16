# Professional Prompt: Implement Real Confluence Integration

## Context

You are working on a **Confluence Publisher** Spring Boot application that currently uses a stub provider for publishing
pages. The application needs to be updated to support real integration with **Atlassian Confluence Server/Data Center**
via REST API.

> **Important**: This integration targets Confluence Server/Data Center (on-premise), NOT Confluence Cloud. The API
> endpoints and authentication differ significantly.

## Project Structure

```
backend/src/main/java/com/confluence/publisher/
├── config/
│   └── AppProperties.java          # Configuration with confluence credentials
├── provider/
│   ├── BaseProvider.java           # Provider interface
│   ├── ConfluenceStubProvider.java # Current stub implementation
│   └── ProviderFactory.java        # Provider selection factory
├── service/
│   └── PublishService.java         # Orchestrates publishing via provider
├── entity/
│   ├── Page.java                   # Page entity (title, content, spaceKey, parentPageId)
│   ├── Attachment.java             # Attachment entity (filename, contentType, size, storagePath)
│   ├── PageAttachment.java         # Page-Attachment relationship
│   └── PublishLog.java             # Publishing log entity
└── scheduler/
    └── PageScheduler.java          # Scheduled publishing
```

## Current Implementation

### BaseProvider Interface

```java
public interface BaseProvider {
    ProviderResult publishPage(
            String spaceKey,
            String title,
            String content,
            Long parentPageId,
            List<String> attachmentPaths
    );

    String getStatus(String confluencePageId);

    record ProviderResult(String confluencePageId, String message) {
    }
}
```

### AppProperties (Configuration)

```java

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String confluenceUrl = "https://your-domain.atlassian.net";
    private String confluenceUsername = "";
    private String confluenceDefaultSpace = "DEV";
    private String confluenceApiToken = "";
    private String provider = "confluence-stub";
    // ... other properties
}
```

### ProviderFactory

```java
public BaseProvider getProvider() {
    String providerName = appProperties.getProvider().toLowerCase();
    return switch (providerName) {
        case "stub" -> stubProvider;
        default -> stubProvider;  // Currently only returns stub
    };
}
```

## Task

Implement a production-ready `ConfluenceServerProvider` class that integrates with the real Confluence Server/Data
Center REST API.

## Requirements

### 1. Create ConfluenceServerProvider

Create a new provider class `ConfluenceServerProvider.java` in the `provider` package that:

- Implements `BaseProvider` interface
- Uses **Confluence Server REST API v1** (`/rest/api/content`)
- Authenticates using **Bearer Token** with Personal Access Token (PAT)
- Base URL format: `https://{domain}/confluence/rest/api`

### 2. Implement publishPage Method

The `publishPage` method must:

1. **Check if page exists** by title in the given space
    - If exists: **Update** the existing page (increment version number)
    - If not exists: **Create** a new page

2. **Handle parent page relationship**
    - If `parentPageId` is provided, set it as the parent
    - The `parentPageId` in our system is the **Confluence page ID** (numeric string)

3. **Upload attachments** after page creation/update
    - Read files from `attachmentPaths` (local file system paths)
    - Upload each attachment to the created/updated page
    - Handle attachment updates (replace if exists)

4. **Return proper ProviderResult**
    - `confluencePageId`: The actual Confluence page ID
    - `message`: Success message with page URL or error details

### 3. Implement getStatus Method

Return the current status of a Confluence page:

- "published" if page exists and is accessible
- "not_found" if page doesn't exist
- "error" with details if API call fails

### 4. Update ProviderFactory

Modify `ProviderFactory.java` to:

- Inject `ConfluenceServerProvider`
- Add case `"confluence"` or `"confluence-server"` to return the real provider
- Keep `"stub"` and `"confluence-stub"` for the stub provider

### 5. Error Handling

Implement robust error handling:

- Create custom exception `ConfluenceApiException` for API errors
- Handle HTTP errors (401, 403, 404, 429, 5xx)
- Implement retry logic with exponential backoff for transient failures (429, 5xx)
- Log all API interactions at appropriate levels (DEBUG for success, WARN/ERROR for failures)

### 6. HTTP Client Configuration

- Use Spring's `RestClient` (Spring Boot 3.x) or `WebClient` for HTTP calls
- Configure timeouts (connect: 10s, read: 30s)
- Add proper headers: `Content-Type`, `Accept`, `Authorization`

### 7. Content Handling

- The `content` parameter contains **Confluence Storage Format** (XHTML-based)
- Ensure proper encoding when sending to API
- Handle large content appropriately

## Confluence Server REST API Reference

### Authentication

Confluence Server/Data Center uses **Bearer Token** authentication with Personal Access Token (PAT):

```
Authorization: Bearer <personal_access_token>
```

> **Note**: Do NOT use Basic Auth with username:PAT. The PAT should be sent directly as a Bearer token.

### Key Endpoints (REST API v1)

Base URL: `https://{domain}/confluence/rest/api`

**Create Page:**

```
POST /rest/api/content
{
  "type": "page",
  "title": "<title>",
  "space": {
    "key": "<space-key>"
  },
  "ancestors": [{"id": "<parent-page-id>"}],  // optional, for child pages
  "body": {
    "storage": {
      "value": "<content>",
      "representation": "storage"
    }
  }
}
```

**Update Page:**

```
PUT /rest/api/content/{page-id}
{
  "type": "page",
  "title": "<title>",
  "body": {
    "storage": {
      "value": "<content>",
      "representation": "storage"
    }
  },
  "version": {
    "number": <current-version + 1>
  }
}
```

**Get Page by Title in Space:**

```
GET /rest/api/content?spaceKey=<space-key>&title=<title>&expand=version
```

**Get Space by Key:**

```
GET /rest/api/space?keys=<space-key>
```

**Upload Attachment:**

```
POST /rest/api/content/{page-id}/child/attachment
Content-Type: multipart/form-data
X-Atlassian-Token: nocheck
```

**Get Page by ID:**

```
GET /rest/api/content/{page-id}?expand=version
```

**Get Current User (for connection test):**

```
GET /rest/api/user/current
```

## Configuration

The user will provide:

- **Confluence URL**: e.g., `https://pmc-stage.specific-group.eu/confluence/`
- **Username**: Confluence username (e.g., `spg.academy`)
- **API Token**: Personal Access Token (PAT) generated from Confluence profile settings
- **Space Key**: Target space key (e.g., `SPGAC`)

Update `application.yml` to support:

```yaml
app:
  confluence-url: ${CONFLUENCE_URL:https://pmc-stage.specific-group.eu/confluence/}
  confluence-username: ${CONFLUENCE_USERNAME:spg.academy}
  confluence-api-token: ${CONFLUENCE_API_TOKEN:}
  confluence-default-space: ${CONFLUENCE_SPACE_KEY:SPGAC}
  provider: ${CONFLUENCE_PROVIDER:confluence-stub}
```

### Environment Variables

```bash
CONFLUENCE_URL=https://pmc-stage.specific-group.eu/confluence/
CONFLUENCE_USERNAME=spg.academy
CONFLUENCE_API_TOKEN=<your-personal-access-token>
CONFLUENCE_SPACE_KEY=SPGAC
CONFLUENCE_PROVIDER=confluence
```

## Dependencies

Add to `build.gradle.kts` if needed:

```kotlin
// For RestClient (already included in spring-boot-starter-web)
// No additional dependencies required for basic HTTP client
```

## Testing Considerations

- Create integration tests with WireMock for API mocking
- Test scenarios: create, update, attachment upload, error handling
- Ensure backward compatibility with stub provider

## Deliverables

1. `ConfluenceServerProvider.java` - Main provider implementation for Confluence Server/DC
2. `ConfluenceApiException.java` - Custom exception class
3. Updated `ProviderFactory.java` - Provider selection logic
4. Updated `AppProperties.java` - If additional config needed
5. Unit tests for the new provider

## Quality Checklist

- [ ] Follows existing code style and patterns
- [ ] Uses Lombok annotations consistently (@Slf4j, @RequiredArgsConstructor)
- [ ] Proper Spring component annotation (@Component)
- [ ] Thread-safe implementation
- [ ] No hardcoded values (use configuration)
- [ ] Comprehensive logging
- [ ] Graceful error handling with meaningful messages
- [ ] API rate limiting awareness
- [ ] Attachment size limits handling
- [ ] Content encoding handled correctly

## Notes

- The application uses **Java 21** and **Spring Boot 3.4.1**
- Database is SQLite (for local storage, not relevant to Confluence integration)
- The `parentPageId` in the Page entity stores the Confluence page ID as a Long
- Attachments are stored locally; paths are relative to `app.attachment-dir` configuration
- **Target Confluence**: Server/Data Center (NOT Cloud)
- **API Version**: REST API v1 (`/rest/api/content`)
- **Authentication**: Bearer Token with Personal Access Token (PAT)

## Key Differences: Confluence Server vs Cloud

| Aspect            | Server/Data Center           | Cloud                            |
|-------------------|------------------------------|----------------------------------|
| URL Pattern       | `/confluence/rest/api`       | `/wiki/api/v2`                   |
| Auth              | Bearer token (PAT)           | Basic (email:api_token) or OAuth |
| Space Reference   | `space.key`                  | `spaceId`                        |
| Parent Page       | `ancestors[{id}]`            | `parentId`                       |
| Attachment Header | `X-Atlassian-Token: nocheck` | Not required                     |

## Docker/Podman Configuration

### Important: Environment Variable Handling

When using Docker/Podman, ensure the **Dockerfile does NOT hardcode** provider-related environment variables that would
override your `.env` file settings.

**Correct Dockerfile ENV section:**

```dockerfile
ENV SPRING_PROFILES_ACTIVE=docker \
    APP_DATABASE_URL=jdbc:sqlite:///data/app.db \
    APP_ATTACHMENT_DIR=/storage/attachments \
    APP_SCHEDULER_INTERVAL_SECONDS=5
# Do NOT set APP_PROVIDER here - let it come from .env via CONFLUENCE_PROVIDER
```

**Correct application-docker.yml:**

```yaml
app:
  provider: ${CONFLUENCE_PROVIDER:confluence-stub}
```

### .env File Example

```bash
# Confluence API Configuration
CONFLUENCE_URL=https://your-confluence-server.com/confluence/
CONFLUENCE_USERNAME=your-username
CONFLUENCE_API_TOKEN=your-personal-access-token
CONFLUENCE_DEFAULT_SPACE=YOURSPACE

# Use "confluence" for real integration, "confluence-stub" for testing
CONFLUENCE_PROVIDER=confluence
```

## Implementation Example: RestClient Configuration

```java
private RestClient createRestClient() {
    String baseUrl = normalizeBaseUrl(appProperties.getConfluenceUrl());
    String apiToken = appProperties.getConfluenceApiToken();

    return RestClient.builder()
            .baseUrl(baseUrl + "/rest/api")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
}
```

## Common Issues and Solutions

| Issue                    | Cause                                  | Solution                                  |
|--------------------------|----------------------------------------|-------------------------------------------|
| HTTP 401 Unauthorized    | Wrong auth method                      | Use Bearer token, not Basic Auth          |
| Stub provider still used | Hardcoded `APP_PROVIDER` in Dockerfile | Remove `APP_PROVIDER` from Dockerfile ENV |
| Lombok @Builder error    | Using `final` with `@Builder`          | Use `@Builder.Default` annotation instead |
