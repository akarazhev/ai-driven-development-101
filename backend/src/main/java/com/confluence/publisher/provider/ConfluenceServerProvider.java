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
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class ConfluenceServerProvider implements BaseProvider {

    private static final int MAX_RETRIES = 3;
    private static final Duration INITIAL_BACKOFF = Duration.ofSeconds(1);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(30);

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public ConfluenceServerProvider(AppProperties appProperties, ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
        this.restClient = createRestClient();
    }

    private RestClient createRestClient() {
        String baseUrl = normalizeBaseUrl(appProperties.getConfluenceUrl());
        String apiToken = appProperties.getConfluenceApiToken();

        log.info("Initializing Confluence Server provider with base URL: {}", baseUrl);

        return RestClient.builder()
                .baseUrl(baseUrl + "/rest/api")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private String normalizeBaseUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new ConfluenceApiException("Confluence URL is not configured");
        }
        String normalized = url.trim();
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        if (normalized.endsWith("/rest/api")) {
            normalized = normalized.substring(0, normalized.length() - "/rest/api".length());
        }
        return normalized;
    }

    @Override
    public ProviderResult publishPage(
            String spaceKey,
            String title,
            String content,
            Long parentPageId,
            List<String> attachmentPaths
    ) {
        log.info("Publishing page '{}' to space '{}' (parent: {})", title, spaceKey, parentPageId);

        try {
            JsonNode existingPage = findPageByTitle(spaceKey, title);
            String pageId;
            String pageUrl;

            if (existingPage != null) {
                log.info("Page '{}' exists, updating...", title);
                pageId = existingPage.get("id").asText();
                int currentVersion = existingPage.get("version").get("number").asInt();
                updatePage(pageId, title, content, currentVersion);
                pageUrl = buildPageUrl(pageId);
                log.info("Page updated successfully. ID: {}, URL: {}", pageId, pageUrl);
            } else {
                log.info("Page '{}' does not exist, creating...", title);
                JsonNode createdPage = createPage(spaceKey, title, content, parentPageId);
                pageId = createdPage.get("id").asText();
                pageUrl = buildPageUrl(pageId);
                log.info("Page created successfully. ID: {}, URL: {}", pageId, pageUrl);
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
        log.debug("Getting status for page ID: {}", confluencePageId);

        if (!StringUtils.hasText(confluencePageId)) {
            return "not_found";
        }

        try {
            JsonNode page = getPageById(confluencePageId);
            if (page != null) {
                log.debug("Page {} found and accessible", confluencePageId);
                return "published";
            }
            return "not_found";
        } catch (ConfluenceApiException e) {
            if (e.isNotFound()) {
                return "not_found";
            }
            log.error("Error getting status for page {}: {}", confluencePageId, e.getMessage());
            return "error: " + e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error getting status for page {}: {}", confluencePageId, e.getMessage());
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

            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.get("results");

            if (results != null && results.isArray() && !results.isEmpty()) {
                log.debug("Found existing page '{}' in space '{}'", title, spaceKey);
                return results.get(0);
            }

            log.debug("Page '{}' not found in space '{}'", title, spaceKey);
            return null;
        });
    }

    private JsonNode createPage(String spaceKey, String title, String content, Long parentPageId) {
        log.debug("Creating page '{}' in space '{}' with parent: {}", title, spaceKey, parentPageId);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("type", "page");
        requestBody.put("title", title);

        ObjectNode space = objectMapper.createObjectNode();
        space.put("key", spaceKey);
        requestBody.set("space", space);

        if (parentPageId != null) {
            ArrayNode ancestors = objectMapper.createArrayNode();
            ObjectNode ancestor = objectMapper.createObjectNode();
            ancestor.put("id", parentPageId.toString());
            ancestors.add(ancestor);
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
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
                        handleErrorResponse(clientResponse.getStatusCode().value(),
                                "Failed to create page");
                    })
                    .body(String.class);

            return objectMapper.readTree(response);
        });
    }

    private void updatePage(String pageId, String title, String content, int currentVersion) {
        log.debug("Updating page '{}' (ID: {}) to version {}", title, pageId, currentVersion + 1);

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
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
                        handleErrorResponse(clientResponse.getStatusCode().value(),
                                "Failed to update page");
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
                uploadSingleAttachment(pageId, attachmentPath);
            } catch (Exception e) {
                log.error("Failed to upload attachment '{}': {}", attachmentPath, e.getMessage());
            }
        }
    }

    private void uploadSingleAttachment(String pageId, String attachmentPath) {
        Path filePath = resolveAttachmentPath(attachmentPath);

        if (!Files.exists(filePath)) {
            log.warn("Attachment file not found: {}", filePath);
            return;
        }

        String fileName = filePath.getFileName().toString();
        String contentType = determineContentType(filePath);

        log.debug("Uploading attachment '{}' (type: {}) to page {}", fileName, contentType, pageId);

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", new FileSystemResource(filePath.toFile()))
                .filename(fileName)
                .contentType(MediaType.parseMediaType(contentType));

        executeWithRetry(() -> {
            restClient.post()
                    .uri("/content/{pageId}/child/attachment", pageId)
                    .header("X-Atlassian-Token", "nocheck")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(bodyBuilder.build())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
                        handleErrorResponse(clientResponse.getStatusCode().value(),
                                "Failed to upload attachment: " + fileName);
                    })
                    .body(String.class);

            log.debug("Successfully uploaded attachment '{}'", fileName);
            return null;
        });
    }

    private Path resolveAttachmentPath(String attachmentPath) {
        Path path = Paths.get(attachmentPath);
        if (path.isAbsolute()) {
            return path;
        }
        return Paths.get(appProperties.getAttachmentDir()).resolve(attachmentPath);
    }

    private String determineContentType(Path filePath) {
        try {
            String contentType = Files.probeContentType(filePath);
            if (contentType != null) {
                return contentType;
            }
        } catch (IOException e) {
            log.debug("Could not determine content type for {}: {}", filePath, e.getMessage());
        }
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    private String buildPageUrl(String pageId) {
        String baseUrl = normalizeBaseUrl(appProperties.getConfluenceUrl());
        return baseUrl + "/pages/viewpage.action?pageId=" + pageId;
    }

    private void handleErrorResponse(int statusCode, String context) {
        String message = switch (statusCode) {
            case 401 -> context + ": Unauthorized - check your API token";
            case 403 -> context + ": Forbidden - insufficient permissions";
            case 404 -> context + ": Not found";
            case 429 -> context + ": Rate limited - too many requests";
            default -> {
                if (statusCode >= 500) {
                    yield context + ": Server error (" + statusCode + ")";
                }
                yield context + ": HTTP error " + statusCode;
            }
        };

        throw new ConfluenceApiException(message, statusCode, null);
    }

    private <T> T executeWithRetry(RetryableOperation<T> operation) {
        int attempt = 0;
        Duration backoff = INITIAL_BACKOFF;

        while (true) {
            try {
                return operation.execute();
            } catch (ConfluenceApiException e) {
                attempt++;
                if (!e.isRetryable() || attempt >= MAX_RETRIES) {
                    throw e;
                }

                log.warn("Retryable error (attempt {}/{}): {}. Retrying in {}ms...",
                        attempt, MAX_RETRIES, e.getMessage(), backoff.toMillis());

                try {
                    Thread.sleep(backoff.toMillis());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new ConfluenceApiException("Operation interrupted", e);
                }

                backoff = backoff.multipliedBy(2);
            } catch (Exception e) {
                throw new ConfluenceApiException("Operation failed: " + e.getMessage(), e);
            }
        }
    }

    @FunctionalInterface
    private interface RetryableOperation<T> {
        T execute() throws Exception;
    }
}
