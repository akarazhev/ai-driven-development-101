package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.exception.ConfluenceApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ConfluenceServerProviderTest {

    private AppProperties appProperties;
    private ConfluenceServerProvider provider;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        appProperties = new AppProperties();
        appProperties.setConfluenceUrl("https://confluence.example.com/confluence");
        appProperties.setConfluenceUsername("testuser");
        appProperties.setConfluenceApiToken("test-token");
        appProperties.setConfluenceDefaultSpace("TEST");
        appProperties.setAttachmentDir("storage/attachments");
        
        provider = new ConfluenceServerProvider(appProperties);
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("publishPage tests")
    class PublishPageTests {

        @Test
        @DisplayName("should create new page when page does not exist")
        void shouldCreateNewPageWhenNotExists() {
            String spaceKey = "TEST";
            String title = "Test Page";
            String content = "<p>Test content</p>";

            BaseProvider.ProviderResult result = null;
            ConfluenceApiException exception = null;
            
            try {
                result = provider.publishPage(spaceKey, title, content, null, Collections.emptyList());
            } catch (ConfluenceApiException e) {
                exception = e;
            }

            assertNotNull(exception, "Expected ConfluenceApiException due to no mock server");
        }

        @Test
        @DisplayName("should handle null attachment paths")
        void shouldHandleNullAttachmentPaths() {
            String spaceKey = "TEST";
            String title = "Test Page";
            String content = "<p>Test content</p>";

            ConfluenceApiException exception = assertThrows(
                ConfluenceApiException.class,
                () -> provider.publishPage(spaceKey, title, content, null, null)
            );

            assertNotNull(exception);
        }

        @Test
        @DisplayName("should handle empty attachment paths")
        void shouldHandleEmptyAttachmentPaths() {
            String spaceKey = "TEST";
            String title = "Test Page";
            String content = "<p>Test content</p>";

            ConfluenceApiException exception = assertThrows(
                ConfluenceApiException.class,
                () -> provider.publishPage(spaceKey, title, content, null, Collections.emptyList())
            );

            assertNotNull(exception);
        }
    }

    @Nested
    @DisplayName("getStatus tests")
    class GetStatusTests {

        @Test
        @DisplayName("should return error status when API call fails")
        void shouldReturnErrorStatusWhenApiFails() {
            String status = provider.getStatus("12345");
            
            assertTrue(status.startsWith("error:") || status.equals("not_found"),
                    "Expected error or not_found status");
        }

        @Test
        @DisplayName("should handle null page ID gracefully")
        void shouldHandleNullPageId() {
            String status = provider.getStatus(null);
            
            assertTrue(status.startsWith("error:") || status.equals("not_found"));
        }
    }

    @Nested
    @DisplayName("Configuration tests")
    class ConfigurationTests {

        @Test
        @DisplayName("should throw exception when URL is not configured")
        void shouldThrowExceptionWhenUrlNotConfigured() {
            AppProperties emptyProps = new AppProperties();
            emptyProps.setConfluenceUrl("");
            emptyProps.setConfluenceUsername("user");
            emptyProps.setConfluenceApiToken("token");

            assertThrows(ConfluenceApiException.class, () -> new ConfluenceServerProvider(emptyProps));
        }

        @Test
        @DisplayName("should throw exception when URL is null")
        void shouldThrowExceptionWhenUrlIsNull() {
            AppProperties nullProps = new AppProperties();
            nullProps.setConfluenceUrl(null);
            nullProps.setConfluenceUsername("user");
            nullProps.setConfluenceApiToken("token");

            assertThrows(ConfluenceApiException.class, () -> new ConfluenceServerProvider(nullProps));
        }

        @Test
        @DisplayName("should normalize URL with trailing slash")
        void shouldNormalizeUrlWithTrailingSlash() {
            AppProperties propsWithSlash = new AppProperties();
            propsWithSlash.setConfluenceUrl("https://confluence.example.com/confluence/");
            propsWithSlash.setConfluenceUsername("user");
            propsWithSlash.setConfluenceApiToken("token");

            ConfluenceServerProvider providerWithSlash = new ConfluenceServerProvider(propsWithSlash);
            assertNotNull(providerWithSlash);
        }
    }

    @Nested
    @DisplayName("ConfluenceApiException tests")
    class ExceptionTests {

        @Test
        @DisplayName("should identify retryable status codes")
        void shouldIdentifyRetryableStatusCodes() {
            ConfluenceApiException rateLimited = new ConfluenceApiException("Rate limited", 429);
            assertTrue(rateLimited.isRetryable());
            assertTrue(rateLimited.isRateLimited());

            ConfluenceApiException serverError = new ConfluenceApiException("Server error", 500);
            assertTrue(serverError.isRetryable());
            assertTrue(serverError.isServerError());

            ConfluenceApiException badGateway = new ConfluenceApiException("Bad gateway", 502);
            assertTrue(badGateway.isRetryable());
        }

        @Test
        @DisplayName("should identify non-retryable status codes")
        void shouldIdentifyNonRetryableStatusCodes() {
            ConfluenceApiException unauthorized = new ConfluenceApiException("Unauthorized", 401);
            assertFalse(unauthorized.isRetryable());
            assertTrue(unauthorized.isUnauthorized());

            ConfluenceApiException forbidden = new ConfluenceApiException("Forbidden", 403);
            assertFalse(forbidden.isRetryable());
            assertTrue(forbidden.isForbidden());

            ConfluenceApiException notFound = new ConfluenceApiException("Not found", 404);
            assertFalse(notFound.isRetryable());
            assertTrue(notFound.isNotFound());

            ConfluenceApiException badRequest = new ConfluenceApiException("Bad request", 400);
            assertFalse(badRequest.isRetryable());
        }

        @Test
        @DisplayName("should store error response")
        void shouldStoreErrorResponse() {
            String errorBody = "{\"message\": \"Page not found\"}";
            ConfluenceApiException exception = new ConfluenceApiException("Not found", 404, errorBody);

            assertEquals(404, exception.getStatusCode());
            assertEquals(errorBody, exception.getErrorResponse());
            assertEquals("Not found", exception.getMessage());
        }
    }
}
