package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConfluenceApiProvider implements BaseProvider {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate;

    private static final String API_BASE_PATH = "/rest/api/content";

    @Override
    public ProviderResult publishPage(
            String spaceKey,
            String title,
            String content,
            Long parentPageId,
            List<String> attachmentPaths
    ) {
        try {
            log.info("Publishing page '{}' to Confluence space '{}' (parent: {})", 
                     title, spaceKey, parentPageId);
            log.info("Confluence URL: {}", appProperties.getConfluenceUrl());
            log.debug("Using username: {}", appProperties.getConfluenceUsername());

            // Step 1: Create the page
            String pageId = createPage(spaceKey, title, content, parentPageId);
            
            // Step 2: Upload attachments if any
            if (attachmentPaths != null && !attachmentPaths.isEmpty()) {
                uploadAttachments(pageId, attachmentPaths);
            }

            log.info("Successfully published page '{}' with ID: {}", title, pageId);
            return new ProviderResult(pageId, "Successfully published to Confluence");
            
        } catch (RestClientException e) {
            log.error("Failed to publish page to Confluence: {}", e.getMessage(), e);
            throw new ServiceException("Failed to publish page to Confluence: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error publishing page to Confluence: {}", e.getMessage(), e);
            throw new ServiceException("Unexpected error publishing page: " + e.getMessage(), e);
        }
    }

    @Override
    public String getStatus(String confluencePageId) {
        try {
            String url = buildApiUrl("/" + confluencePageId);
            HttpEntity<String> entity = createHttpEntity();
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, 
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return "published";
            }
            return "unknown";
        } catch (Exception e) {
            log.warn("Failed to get status for Confluence page {}: {}", confluencePageId, e.getMessage());
            return "unknown";
        }
    }

    private String createPage(String spaceKey, String title, String content, Long parentPageId) {
        String url = buildApiUrl("");
        log.info("Creating page at URL: {}", url);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", "page");
        requestBody.put("title", title);
        
        // Space
        Map<String, String> space = new HashMap<>();
        space.put("key", spaceKey);
        requestBody.put("space", space);
        
        // Body (content)
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> storage = new HashMap<>();
        storage.put("value", content);
        storage.put("representation", "storage");
        body.put("storage", storage);
        requestBody.put("body", body);
        
        // Parent page (if specified)
        if (parentPageId != null) {
            Map<String, Object> ancestors = new HashMap<>();
            ancestors.put("id", parentPageId.toString());
            requestBody.put("ancestors", List.of(ancestors));
        }

        HttpEntity<Map<String, Object>> entity = createHttpEntity(requestBody);
        
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, 
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Object idObj = responseBody.get("id");
                if (idObj != null) {
                    String pageId = idObj.toString();
                    log.info("Created Confluence page with ID: {}", pageId);
                    return pageId;
                } else {
                    throw new ServiceException("Confluence API did not return page ID");
                }
            } else {
                throw new ServiceException("Failed to create page: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            log.error("REST client error creating page at URL {}: {}", url, e.getMessage());
            log.error("Check authentication: Using Bearer token for Confluence Server/Data Center");
            throw new ServiceException("Failed to create page in Confluence: " + e.getMessage(), e);
        }
    }

    private void uploadAttachments(String pageId, List<String> attachmentPaths) {
        log.info("Uploading {} attachments to page {}", attachmentPaths.size(), pageId);
        
        for (String attachmentPath : attachmentPaths) {
            try {
                uploadAttachment(pageId, attachmentPath);
            } catch (Exception e) {
                log.warn("Failed to upload attachment {}: {}", attachmentPath, e.getMessage());
                // Continue with other attachments
            }
        }
    }

    private void uploadAttachment(String pageId, String attachmentPath) {
        // Note: This is a simplified implementation
        // Full attachment upload requires multipart/form-data and proper file handling
        // For now, we'll log that attachments should be uploaded
        log.info("Attachment upload for page {} and file {} - requires multipart implementation", 
                 pageId, attachmentPath);
        
        // TODO: Implement full multipart file upload using Confluence REST API
        // POST /rest/api/content/{pageId}/child/attachment
    }

    private String buildApiUrl(String path) {
        String baseUrl = appProperties.getConfluenceUrl();
        // Remove trailing slash if present
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + API_BASE_PATH + path;
    }

    private HttpEntity<String> createHttpEntity() {
        HttpHeaders headers = createHeaders();
        return new HttpEntity<>(headers);
    }

    private <T> HttpEntity<T> createHttpEntity(T body) {
        HttpHeaders headers = createHeaders();
        return new HttpEntity<>(body, headers);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        
        String username = appProperties.getConfluenceUsername();
        String token = appProperties.getConfluenceApiToken();
        
        if (token != null && !token.isEmpty()) {
            // Check if this is a Personal Access Token (PAT) for Confluence Server/Data Center
            // PATs are typically used with Bearer authentication
            // If username is empty or the URL contains server indicators, use Bearer auth
            String confluenceUrl = appProperties.getConfluenceUrl();
            boolean isServerInstance = confluenceUrl != null && 
                    (confluenceUrl.contains("/confluence") || !confluenceUrl.contains("atlassian.net"));
            
            if (isServerInstance || username == null || username.isEmpty()) {
                // Use Bearer authentication for Personal Access Token (Confluence Server/Data Center)
                log.debug("Using Bearer authentication for Confluence Server/Data Center");
                headers.set("Authorization", "Bearer " + token);
            } else {
                // Use Basic authentication for Atlassian Cloud (username:api-token)
                log.debug("Using Basic authentication for Atlassian Cloud");
                String credentials = username + ":" + token;
                String encodedCredentials = Base64.getEncoder().encodeToString(
                        credentials.getBytes(StandardCharsets.UTF_8));
                headers.set("Authorization", "Basic " + encodedCredentials);
            }
        } else {
            log.warn("Confluence API token not configured. API calls may fail.");
        }
        
        return headers;
    }
}

