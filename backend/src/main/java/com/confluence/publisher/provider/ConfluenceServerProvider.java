package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.exception.ConfluenceApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
public class ConfluenceServerProvider implements BaseProvider {

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_RETRY_DELAY_MS = 1000;
    private static final String REST_API_PATH = "/rest/api";

    private final AppProperties appProperties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ConfluenceServerProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
        this.objectMapper = new ObjectMapper();
        this.restClient = createRestClient();
    }

    private RestClient createRestClient() {
        String baseUrl = normalizeBaseUrl(appProperties.getConfluenceUrl());
        String apiToken = appProperties.getConfluenceApiToken();

        return RestClient.builder()
                .baseUrl(baseUrl + REST_API_PATH)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private String normalizeBaseUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new ConfluenceApiException("Confluence URL is not configured");
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    @Override
    public ProviderResult publishPage(
            String spaceKey,
            String title,
            String content,
            Long parentPageId,
            List<String> attachmentPaths
    ) {
        log.info("Publishing page '{}' to Confluence space '{}'", title, spaceKey);

        try {
            JsonNode existingPage = findPageByTitle(spaceKey, title);
            String pageId;
            String pageUrl;

            if (existingPage != null) {
                pageId = existingPage.get("id").asText();
                int currentVersion = existingPage.get("version").get("number").asInt();
                log.info("Page '{}' exists with ID {}, updating (version {})", title, pageId, currentVersion);
                updatePage(pageId, title, content, currentVersion);
                pageUrl = buildPageUrl(pageId);
                log.info("Successfully updated page '{}' (ID: {})", title, pageId);
            } else {
                log.info("Page '{}' does not exist, creating new page", title);
                JsonNode createdPage = createPage(spaceKey, title, content, parentPageId);
                pageId = createdPage.get("id").asText();
                pageUrl = buildPageUrl(pageId);
                log.info("Successfully created page '{}' (ID: {})", title, pageId);
            }

            if (attachmentPaths != null && !attachmentPaths.isEmpty()) {
                uploadAttachments(pageId, attachmentPaths);
            }

            return new ProviderResult(pageId, "Successfully published to Confluence: " + pageUrl);

        } catch (ConfluenceApiException e) {
            log.error("Confluence API error while publishing page '{}': {}", title, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while publishing page '{}': {}", title, e.getMessage(), e);
            throw new ConfluenceApiException("Failed to publish page: " + e.getMessage(), e);
        }
    }

    @Override
    public String getStatus(String confluencePageId) {
        log.debug("Getting status for Confluence page {}", confluencePageId);

        try {
            JsonNode page = getPageById(confluencePageId);
            if (page != null) {
                log.debug("Page {} found and accessible", confluencePageId);
                return "published";
            }
            return "not_found";
        } catch (ConfluenceApiException e) {
            if (e.isNotFound()) {
                log.debug("Page {} not found", confluencePageId);
                return "not_found";
            }
            log.warn("Error checking status for page {}: {}", confluencePageId, e.getMessage());
            return "error: " + e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error checking status for page {}: {}", confluencePageId, e.getMessage());
            return "error: " + e.getMessage();
        }
    }

    private JsonNode findPageByTitle(String spaceKey, String title) {
        log.debug("Searching for page '{}' in space '{}'", title, spaceKey);

        return executeWithRetry(() -> {
            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/content")
                            .queryParam("spaceKey", spaceKey)
                            .queryParam("title", title)
                            .queryParam("expand", "version")
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
                        handleErrorResponse(clientResponse.getStatusCode().value(), 
                                "Failed to search for page");
                    })
                    .body(String.class);

            JsonNode result = objectMapper.readTree(response);
            JsonNode results = result.get("results");

            if (results != null && results.isArray() && !results.isEmpty()) {
                log.debug("Found page '{}' in space '{}'", title, spaceKey);
                return results.get(0);
            }

            log.debug("Page '{}' not found in space '{}'", title, spaceKey);
            return null;
        });
    }

    private JsonNode createPage(String spaceKey, String title, String content, Long parentPageId) {
        log.debug("Creating page '{}' in space '{}' with parent {}", title, spaceKey, parentPageId);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("type", "page");
        requestBody.put("title", title);

        ObjectNode space = objectMapper.createObjectNode();
        space.put("key", spaceKey);
        requestBody.set("space", space);

        if (parentPageId != null) {
            ArrayNode ancestors = objectMapper.createArrayNode();
            ObjectNode parent = objectMapper.createObjectNode();
            parent.put("id", parentPageId.toString());
            ancestors.add(parent);
            requestBody.set("ancestors", ancestors);
        }

        ObjectNode body = objectMapper.createObjectNode();
        ObjectNode storage = objectMapper.createObjectNode();
        storage.put("value", content);
        storage.put("representation", "storage");
        body.set("storage", storage);
        requestBody.set("body", body);

        return executeWithRetry(() -> {
            String response = restClient.post()
                    .uri("/content")
                    .body(requestBody.toString())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
                        String errorBody = new String(clientResponse.getBody().readAllBytes(), StandardCharsets.UTF_8);
                        handleErrorResponse(clientResponse.getStatusCode().value(), 
                                "Failed to create page", errorBody);
                    })
                    .body(String.class);

            return objectMapper.readTree(response);
        });
    }

    private void updatePage(String pageId, String title, String content, int currentVersion) {
        log.debug("Updating page {} to version {}", pageId, currentVersion + 1);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("type", "page");
        requestBody.put("title", title);

        ObjectNode body = objectMapper.createObjectNode();
        ObjectNode storage = objectMapper.createObjectNode();
        storage.put("value", content);
        storage.put("representation", "storage");
        body.set("storage", storage);
        requestBody.set("body", body);

        ObjectNode version = objectMapper.createObjectNode();
        version.put("number", currentVersion + 1);
        requestBody.set("version", version);

        executeWithRetry(() -> {
            restClient.put()
                    .uri("/content/{pageId}", pageId)
                    .body(requestBody.toString())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
                        String errorBody = new String(clientResponse.getBody().readAllBytes(), StandardCharsets.UTF_8);
                        handleErrorResponse(clientResponse.getStatusCode().value(), 
                                "Failed to update page", errorBody);
                    })
                    .body(String.class);
            return null;
        });
    }

    private JsonNode getPageById(String pageId) {
        log.debug("Getting page by ID: {}", pageId);

        return executeWithRetry(() -> {
            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/content/{pageId}")
                            .queryParam("expand", "version")
                            .build(pageId))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
                        handleErrorResponse(clientResponse.getStatusCode().value(), 
                                "Failed to get page");
                    })
                    .body(String.class);

            return objectMapper.readTree(response);
        });
    }

    private void uploadAttachments(String pageId, List<String> attachmentPaths) {
        log.info("Uploading {} attachments to page {}", attachmentPaths.size(), pageId);

        for (String attachmentPath : attachmentPaths) {
            try {
                uploadAttachment(pageId, attachmentPath);
            } catch (Exception e) {
                log.error("Failed to upload attachment '{}': {}", attachmentPath, e.getMessage());
            }
        }
    }

    private void uploadAttachment(String pageId, String attachmentPath) {
        Path filePath = Path.of(attachmentPath);
        
        if (!Files.exists(filePath)) {
            String fullPath = Path.of(appProperties.getAttachmentDir(), attachmentPath).toString();
            filePath = Path.of(fullPath);
        }

        if (!Files.exists(filePath)) {
            log.warn("Attachment file not found: {}", attachmentPath);
            return;
        }

        String fileName = filePath.getFileName().toString();
        log.debug("Uploading attachment '{}' to page {}", fileName, pageId);

        Path finalFilePath = filePath;
        executeWithRetry(() -> {
            try {
                String contentType = Files.probeContentType(finalFilePath);
                if (contentType == null) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }

                MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
                bodyBuilder.part("file", new FileSystemResource(finalFilePath.toFile()))
                        .filename(fileName)
                        .contentType(MediaType.parseMediaType(contentType));

                restClient.post()
                        .uri("/content/{pageId}/child/attachment", pageId)
                        .header("X-Atlassian-Token", "nocheck")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(bodyBuilder.build())
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
                            String errorBody = new String(clientResponse.getBody().readAllBytes(), StandardCharsets.UTF_8);
                            handleErrorResponse(clientResponse.getStatusCode().value(),
                                    "Failed to upload attachment", errorBody);
                        })
                        .body(String.class);

                log.debug("Successfully uploaded attachment '{}'", fileName);
                return null;
            } catch (IOException e) {
                throw new ConfluenceApiException("Failed to read attachment file: " + e.getMessage(), e);
            }
        });
    }

    private String buildPageUrl(String pageId) {
        String baseUrl = normalizeBaseUrl(appProperties.getConfluenceUrl());
        return baseUrl + "/pages/viewpage.action?pageId=" + pageId;
    }

    private void handleErrorResponse(int statusCode, String message) {
        handleErrorResponse(statusCode, message, null);
    }

    private void handleErrorResponse(int statusCode, String message, String errorBody) {
        String fullMessage = message + " (HTTP " + statusCode + ")";
        
        if (errorBody != null && !errorBody.isBlank()) {
            try {
                JsonNode errorJson = objectMapper.readTree(errorBody);
                if (errorJson.has("message")) {
                    fullMessage += ": " + errorJson.get("message").asText();
                }
            } catch (Exception e) {
                fullMessage += ": " + errorBody;
            }
        }

        switch (statusCode) {
            case 401 -> {
                log.error("Authentication failed: {}", fullMessage);
                throw new ConfluenceApiException("Authentication failed - check username and API token", statusCode, errorBody);
            }
            case 403 -> {
                log.error("Access forbidden: {}", fullMessage);
                throw new ConfluenceApiException("Access forbidden - check permissions", statusCode, errorBody);
            }
            case 404 -> {
                log.warn("Resource not found: {}", fullMessage);
                throw new ConfluenceApiException("Resource not found", statusCode, errorBody);
            }
            case 429 -> {
                log.warn("Rate limited: {}", fullMessage);
                throw new ConfluenceApiException("Rate limited - too many requests", statusCode, errorBody);
            }
            default -> {
                if (statusCode >= 500) {
                    log.error("Server error: {}", fullMessage);
                    throw new ConfluenceApiException("Confluence server error", statusCode, errorBody);
                }
                log.error("API error: {}", fullMessage);
                throw new ConfluenceApiException(fullMessage, statusCode, errorBody);
            }
        }
    }

    private <T> T executeWithRetry(RetryableOperation<T> operation) {
        int attempt = 0;
        long delayMs = INITIAL_RETRY_DELAY_MS;

        while (true) {
            try {
                return operation.execute();
            } catch (ConfluenceApiException e) {
                attempt++;
                if (!e.isRetryable() || attempt >= MAX_RETRIES) {
                    throw e;
                }
                log.warn("Retryable error (attempt {}/{}), retrying in {}ms: {}", 
                        attempt, MAX_RETRIES, delayMs, e.getMessage());
                sleep(delayMs);
                delayMs *= 2;
            } catch (Exception e) {
                throw new ConfluenceApiException("Unexpected error: " + e.getMessage(), e);
            }
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConfluenceApiException("Operation interrupted", e);
        }
    }

    @FunctionalInterface
    private interface RetryableOperation<T> {
        T execute() throws Exception;
    }
}
