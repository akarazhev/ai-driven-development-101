# Prompt 06: Backend Confluence Providers

## Context
Continue building the Confluence Publisher application. Create the provider pattern implementation for Confluence API integration, including a stub provider for testing and a real Confluence Server provider.

## Requirements

### Package Structure
All provider classes should be in `com.confluence.publisher.provider` package with DTOs in `provider/dto/` sub-package.

### Interface: BaseProvider
Create `provider/BaseProvider.java`:
```java
package com.confluence.publisher.provider;

import java.util.List;

public interface BaseProvider {
    
    ProviderResult publishPage(
        String spaceKey,
        String title,
        String content,
        Long parentPageId,
        List<String> attachmentPaths
    );
    
    String getStatus(String confluencePageId);
    
    record ProviderResult(String confluencePageId, String message) {}
}
```

### Provider: ConfluenceStubProvider
Create `provider/ConfluenceStubProvider.java`:
```java
package com.confluence.publisher.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class ConfluenceStubProvider implements BaseProvider {
    
    @Override
    public ProviderResult publishPage(
        String spaceKey,
        String title,
        String content,
        Long parentPageId,
        List<String> attachmentPaths
    ) {
        String pageId = "CONF-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("Stub: Publishing page '{}' to Confluence space '{}' (parent: {})", 
                 title, spaceKey, parentPageId);
        log.info("Stub: Page ID: {}, Attachments: {}", pageId, attachmentPaths.size());
        return new ProviderResult(pageId, "Successfully published to Confluence (stub)");
    }
    
    @Override
    public String getStatus(String confluencePageId) {
        log.info("Stub: Getting status for Confluence page {}", confluencePageId);
        return "published";
    }
}
```

### Factory: ProviderFactory
Create `provider/ProviderFactory.java`:
```java
package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProviderFactory {
    
    private final AppProperties appProperties;
    private final ConfluenceStubProvider stubProvider;
    private final ConfluenceServerProvider serverProvider;
    
    public BaseProvider getProvider() {
        String providerName = appProperties.getProvider().toLowerCase();
        log.debug("Selecting provider: {}", providerName);
        return switch (providerName) {
            case "confluence-server", "server" -> serverProvider;
            case "confluence-stub", "stub" -> stubProvider;
            default -> {
                log.warn("Unknown provider '{}', falling back to stub", providerName);
                yield stubProvider;
            }
        };
    }
    
    public String getProviderName() {
        return appProperties.getProvider();
    }
}
```

### Provider DTOs
Create `provider/dto/ConfluencePageRequest.java`:
```java
package com.confluence.publisher.provider.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfluencePageRequest {
    
    private String type;
    private String title;
    private Space space;
    private Body body;
    private List<Ancestor> ancestors;
    private Version version;
    
    @Data
    @Builder
    public static class Space {
        private String key;
    }
    
    @Data
    @Builder
    public static class Body {
        private Storage storage;
    }
    
    @Data
    @Builder
    public static class Storage {
        private String value;
        private String representation;
    }
    
    @Data
    @Builder
    public static class Ancestor {
        private String id;
    }
    
    @Data
    @Builder
    public static class Version {
        private int number;
    }
}
```

Create `provider/dto/ConfluencePageResponse.java`:
```java
package com.confluence.publisher.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfluencePageResponse {
    
    private String id;
    private String type;
    private String status;
    private String title;
    private Space space;
    private Version version;
    private Links _links;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Space {
        private String key;
        private String name;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Version {
        private int number;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links {
        private String webui;
        private String self;
    }
}
```

Create `provider/dto/ConfluenceSearchResponse.java`:
```java
package com.confluence.publisher.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfluenceSearchResponse {
    
    private List<ConfluencePageResponse> results;
    private int start;
    private int limit;
    private int size;
}
```

### Provider: ConfluenceServerProvider
Create `provider/ConfluenceServerProvider.java`:
```java
package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.provider.dto.ConfluencePageRequest;
import com.confluence.publisher.provider.dto.ConfluencePageResponse;
import com.confluence.publisher.provider.dto.ConfluenceSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
public class ConfluenceServerProvider implements BaseProvider {
    
    private final AppProperties appProperties;
    private volatile RestClient restClient;
    
    public ConfluenceServerProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }
    
    private RestClient getRestClient() {
        if (restClient == null) {
            synchronized (this) {
                if (restClient == null) {
                    restClient = createRestClient();
                }
            }
        }
        return restClient;
    }
    
    private RestClient createRestClient() {
        String baseUrl = appProperties.getConfluenceUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        
        log.info("Initializing Confluence Server provider for: {}", baseUrl);
        
        String apiToken = appProperties.getConfluenceApiToken();
        String authHeader;
        
        // Detect if token is a Personal Access Token (PAT) or password
        // PAT tokens are typically Base64-like strings, passwords are used with Basic auth
        if (apiToken != null && apiToken.length() > 30 && !apiToken.contains(" ")) {
            // Use Bearer token for Personal Access Tokens (Confluence Server 7.9+)
            authHeader = "Bearer " + apiToken;
            log.info("Using Bearer token authentication");
        } else {
            // Fall back to Basic auth with username:password
            String credentials = appProperties.getConfluenceUsername() + ":" + apiToken;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            authHeader = "Basic " + encodedCredentials;
            log.info("Using Basic authentication for user: {}", appProperties.getConfluenceUsername());
        }
        
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    
    @Override
    public ProviderResult publishPage(
            String spaceKey,
            String title,
            String content,
            Long parentPageId,
            List<String> attachmentPaths
    ) {
        try {
            ConfluencePageResponse existingPage = findPageByTitle(spaceKey, title);
            
            ConfluencePageResponse result;
            if (existingPage != null) {
                log.info("Page '{}' exists in space '{}', updating...", title, spaceKey);
                result = updatePage(existingPage, content, parentPageId);
            } else {
                log.info("Creating new page '{}' in space '{}'", title, spaceKey);
                result = createPage(spaceKey, title, content, parentPageId);
            }
            
            if (attachmentPaths != null && !attachmentPaths.isEmpty()) {
                uploadAttachments(result.getId(), attachmentPaths);
            }
            
            String webUrl = buildWebUrl(result);
            return new ProviderResult(result.getId(), "Successfully published to Confluence: " + webUrl);
            
        } catch (RestClientException e) {
            log.error("Failed to publish page '{}' to Confluence", title, e);
            throw new RuntimeException("Confluence API error: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getStatus(String confluencePageId) {
        try {
            ConfluencePageResponse page = getRestClient().get()
                    .uri("/rest/api/content/{id}", confluencePageId)
                    .retrieve()
                    .body(ConfluencePageResponse.class);
            
            return page != null ? page.getStatus() : "unknown";
        } catch (RestClientException e) {
            log.error("Failed to get status for page {}", confluencePageId, e);
            return "error";
        }
    }
    
    private ConfluencePageResponse findPageByTitle(String spaceKey, String title) {
        try {
            ConfluenceSearchResponse searchResponse = getRestClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/api/content")
                            .queryParam("spaceKey", spaceKey)
                            .queryParam("title", title)
                            .queryParam("expand", "version")
                            .build())
                    .retrieve()
                    .body(ConfluenceSearchResponse.class);
            
            if (searchResponse != null && searchResponse.getResults() != null && !searchResponse.getResults().isEmpty()) {
                return searchResponse.getResults().stream()
                        .filter(p -> title.equals(p.getTitle()))
                        .findFirst()
                        .orElse(null);
            }
            return null;
        } catch (RestClientException e) {
            log.debug("Page not found or error searching: {}", e.getMessage());
            return null;
        }
    }
    
    private ConfluencePageResponse createPage(String spaceKey, String title, String content, Long parentPageId) {
        ConfluencePageRequest.ConfluencePageRequestBuilder requestBuilder = ConfluencePageRequest.builder()
                .type("page")
                .title(title)
                .space(ConfluencePageRequest.Space.builder().key(spaceKey).build())
                .body(ConfluencePageRequest.Body.builder()
                        .storage(ConfluencePageRequest.Storage.builder()
                                .value(content)
                                .representation("storage")
                                .build())
                        .build());
        
        if (parentPageId != null) {
            requestBuilder.ancestors(List.of(
                    ConfluencePageRequest.Ancestor.builder()
                            .id(String.valueOf(parentPageId))
                            .build()
            ));
        }
        
        ConfluencePageRequest request = requestBuilder.build();
        
        return getRestClient().post()
                .uri("/rest/api/content")
                .body(request)
                .retrieve()
                .body(ConfluencePageResponse.class);
    }
    
    private ConfluencePageResponse updatePage(ConfluencePageResponse existingPage, String content, Long parentPageId) {
        int newVersion = existingPage.getVersion().getNumber() + 1;
        
        ConfluencePageRequest.ConfluencePageRequestBuilder requestBuilder = ConfluencePageRequest.builder()
                .type("page")
                .title(existingPage.getTitle())
                .version(ConfluencePageRequest.Version.builder().number(newVersion).build())
                .body(ConfluencePageRequest.Body.builder()
                        .storage(ConfluencePageRequest.Storage.builder()
                                .value(content)
                                .representation("storage")
                                .build())
                        .build());
        
        if (parentPageId != null) {
            requestBuilder.ancestors(List.of(
                    ConfluencePageRequest.Ancestor.builder()
                            .id(String.valueOf(parentPageId))
                            .build()
            ));
        }
        
        ConfluencePageRequest request = requestBuilder.build();
        
        return getRestClient().put()
                .uri("/rest/api/content/{id}", existingPage.getId())
                .body(request)
                .retrieve()
                .body(ConfluencePageResponse.class);
    }
    
    private void uploadAttachments(String pageId, List<String> attachmentPaths) {
        for (String attachmentPath : attachmentPaths) {
            try {
                Path filePath = Path.of(attachmentPath);
                if (!filePath.toFile().exists()) {
                    log.warn("Attachment file not found: {}", attachmentPath);
                    continue;
                }
                
                MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
                bodyBuilder.part("file", new FileSystemResource(filePath));
                
                getRestClient().post()
                        .uri("/rest/api/content/{id}/child/attachment", pageId)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                        .header("X-Atlassian-Token", "nocheck")
                        .body(bodyBuilder.build())
                        .retrieve()
                        .toBodilessEntity();
                
                log.info("Uploaded attachment: {}", filePath.getFileName());
            } catch (RestClientException e) {
                log.error("Failed to upload attachment: {}", attachmentPath, e);
            }
        }
    }
    
    private String buildWebUrl(ConfluencePageResponse page) {
        if (page.get_links() != null && page.get_links().getWebui() != null) {
            String baseUrl = appProperties.getConfluenceUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            return baseUrl + page.get_links().getWebui();
        }
        return "Page ID: " + page.getId();
    }
}
```

## Key Design Decisions
1. **Provider pattern**: Abstraction allows switching between stub and real Confluence implementations
2. **Factory pattern**: ProviderFactory selects provider based on configuration
3. **Lazy initialization**: RestClient is created lazily with double-checked locking
4. **Authentication**: Supports both Bearer token (PAT) and Basic auth (username:password)
5. **Upsert logic**: Checks if page exists by title, creates or updates accordingly
6. **Version management**: Increments version number when updating existing pages
7. **Attachment upload**: Uses multipart form data with `X-Atlassian-Token: nocheck` header

## Confluence REST API Endpoints Used
| Operation | Method | Endpoint |
|-----------|--------|----------|
| Search pages | GET | `/rest/api/content?spaceKey=X&title=Y` |
| Create page | POST | `/rest/api/content` |
| Update page | PUT | `/rest/api/content/{id}` |
| Get page | GET | `/rest/api/content/{id}` |
| Upload attachment | POST | `/rest/api/content/{id}/child/attachment` |

## Verification
- Stub provider logs operations without making real API calls
- Server provider connects to Confluence with proper authentication
- Pages are created/updated correctly
- Attachments are uploaded to pages
