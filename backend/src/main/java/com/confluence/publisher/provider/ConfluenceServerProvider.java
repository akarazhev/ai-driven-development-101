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
            String cql = String.format("space=%s AND title=\"%s\" AND type=page", spaceKey, title);
            
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
