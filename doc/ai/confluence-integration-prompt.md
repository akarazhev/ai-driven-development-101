# Professional Prompt: Implement Real Confluence Integration

## Context

You are working on a **Confluence Publisher** Spring Boot application that currently uses a stub provider for publishing
pages. The application needs to be updated to support real integration with Atlassian Confluence Cloud via REST API.

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

Implement a production-ready `ConfluenceCloudProvider` class that integrates with the real Confluence Cloud REST API.

## Requirements

### 1. Create ConfluenceCloudProvider

Create a new provider class `ConfluenceCloudProvider.java` in the `provider` package that:

- Implements `BaseProvider` interface
- Uses **Confluence Cloud REST API v2** (preferred) or v1 as fallback
- Authenticates using **Bearer token** (API token with email as username for Basic Auth, or OAuth Bearer token)
- Base URL format: `https://{domain}.atlassian.net/wiki/api/v2`

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

- Inject `ConfluenceCloudProvider`
- Add case `"confluence"` or `"confluence-cloud"` to return the real provider
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

## Confluence REST API Reference

### Authentication

```
Authorization: Basic base64(email:api_token)
```

Or for Bearer token:

```
Authorization: Bearer <token>
```

### Key Endpoints (API v2)

**Create Page:**

```
POST /wiki/api/v2/pages
{
  "spaceId": "<space-id>",
  "status": "current",
  "title": "<title>",
  "parentId": "<parent-page-id>",  // optional
  "body": {
    "representation": "storage",
    "value": "<content>"
  }
}
```

**Update Page:**

```
PUT /wiki/api/v2/pages/{page-id}
{
  "id": "<page-id>",
  "status": "current",
  "title": "<title>",
  "body": {
    "representation": "storage",
    "value": "<content>"
  },
  "version": {
    "number": <current-version + 1>,
    "message": "Updated via Confluence Publisher"
  }
}
```

**Get Page by Title:**

```
GET /wiki/api/v2/spaces/{space-id}/pages?title=<title>
```

**Get Space by Key:**

```
GET /wiki/api/v2/spaces?keys=<space-key>
```

**Upload Attachment:**

```
POST /wiki/api/v2/pages/{page-id}/attachments
Content-Type: multipart/form-data
```

**Get Page:**

```
GET /wiki/api/v2/pages/{page-id}
```

## Configuration

The user will provide:

- **Confluence URL**: e.g., `https://company.atlassian.net`
- **Username/Email**: The Atlassian account email
- **API Token/Bearer Token**: Generated from Atlassian account settings

Update `application.yml` to support:

```yaml
app:
  confluence-url: ${CONFLUENCE_URL:https://your-domain.atlassian.net}
  confluence-username: ${CONFLUENCE_USERNAME:}
  confluence-api-token: ${CONFLUENCE_API_TOKEN:}
  provider: ${CONFLUENCE_PROVIDER:confluence-stub}
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

1. `ConfluenceCloudProvider.java` - Main provider implementation
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
