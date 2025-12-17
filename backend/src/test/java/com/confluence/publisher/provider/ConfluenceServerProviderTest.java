package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.exception.ConfluenceApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConfluenceServerProvider Tests")
class ConfluenceServerProviderTest {

    @Mock
    private AppProperties appProperties;

    private ObjectMapper objectMapper;
    private ConfluenceServerProvider provider;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("Configuration Tests")
    class ConfigurationTests {

        @Test
        @DisplayName("Should throw exception when Confluence URL is not configured")
        void shouldThrowExceptionWhenUrlNotConfigured() {
            when(appProperties.getConfluenceUrl()).thenReturn("");
            when(appProperties.getConfluenceApiToken()).thenReturn("test-token");

            provider = new ConfluenceServerProvider(appProperties, objectMapper);

            assertThrows(ConfluenceApiException.class, () -> provider.init());
        }

        @Test
        @DisplayName("Should throw exception when Confluence URL is null")
        void shouldThrowExceptionWhenUrlIsNull() {
            when(appProperties.getConfluenceUrl()).thenReturn(null);
            when(appProperties.getConfluenceApiToken()).thenReturn("test-token");

            provider = new ConfluenceServerProvider(appProperties, objectMapper);

            assertThrows(ConfluenceApiException.class, () -> provider.init());
        }

        @Test
        @DisplayName("Should normalize URL with trailing slash")
        void shouldNormalizeUrlWithTrailingSlash() {
            when(appProperties.getConfluenceUrl()).thenReturn("https://confluence.example.com/");
            when(appProperties.getConfluenceApiToken()).thenReturn("test-token");

            provider = new ConfluenceServerProvider(appProperties, objectMapper);

            assertDoesNotThrow(() -> provider.init());
        }

        @Test
        @DisplayName("Should normalize URL with /rest/api suffix")
        void shouldNormalizeUrlWithRestApiSuffix() {
            when(appProperties.getConfluenceUrl()).thenReturn("https://confluence.example.com/rest/api");
            when(appProperties.getConfluenceApiToken()).thenReturn("test-token");

            provider = new ConfluenceServerProvider(appProperties, objectMapper);

            assertDoesNotThrow(() -> provider.init());
        }
    }

    @Nested
    @DisplayName("ConfluenceApiException Tests")
    class ExceptionTests {

        @Test
        @DisplayName("Should create unauthorized exception")
        void shouldCreateUnauthorizedException() {
            ConfluenceApiException ex = ConfluenceApiException.unauthorized("test message");

            assertEquals(401, ex.getStatusCode());
            assertTrue(ex.getMessage().contains("Unauthorized"));
            assertFalse(ex.isRetryable());
        }

        @Test
        @DisplayName("Should create forbidden exception")
        void shouldCreateForbiddenException() {
            ConfluenceApiException ex = ConfluenceApiException.forbidden("test message");

            assertEquals(403, ex.getStatusCode());
            assertTrue(ex.getMessage().contains("Forbidden"));
            assertFalse(ex.isRetryable());
        }

        @Test
        @DisplayName("Should create not found exception")
        void shouldCreateNotFoundException() {
            ConfluenceApiException ex = ConfluenceApiException.notFound("test message");

            assertEquals(404, ex.getStatusCode());
            assertTrue(ex.getMessage().contains("Not found"));
            assertFalse(ex.isRetryable());
        }

        @Test
        @DisplayName("Should create rate limited exception as retryable")
        void shouldCreateRateLimitedExceptionAsRetryable() {
            ConfluenceApiException ex = ConfluenceApiException.rateLimited("test message");

            assertEquals(429, ex.getStatusCode());
            assertTrue(ex.getMessage().contains("Rate limited"));
            assertTrue(ex.isRetryable());
        }

        @Test
        @DisplayName("Should create server error exception as retryable")
        void shouldCreateServerErrorExceptionAsRetryable() {
            ConfluenceApiException ex = ConfluenceApiException.serverError("test message", 500);

            assertEquals(500, ex.getStatusCode());
            assertTrue(ex.getMessage().contains("Server error"));
            assertTrue(ex.isRetryable());
        }

        @Test
        @DisplayName("Should mark 502 as retryable")
        void shouldMark502AsRetryable() {
            ConfluenceApiException ex = ConfluenceApiException.serverError("bad gateway", 502);

            assertTrue(ex.isRetryable());
        }

        @Test
        @DisplayName("Should mark 503 as retryable")
        void shouldMark503AsRetryable() {
            ConfluenceApiException ex = ConfluenceApiException.serverError("service unavailable", 503);

            assertTrue(ex.isRetryable());
        }
    }

    @Nested
    @DisplayName("Provider Factory Tests")
    class ProviderFactoryTests {

        @Mock
        private ConfluenceStubProvider stubProvider;

        @Mock
        private ConfluenceServerProvider serverProvider;

        private ProviderFactory factory;

        @Test
        @DisplayName("Should return confluence server provider for 'confluence'")
        void shouldReturnServerProviderForConfluence() {
            when(appProperties.getProvider()).thenReturn("confluence");
            factory = new ProviderFactory(appProperties, stubProvider, serverProvider);

            BaseProvider result = factory.getProvider();

            assertSame(serverProvider, result);
        }

        @Test
        @DisplayName("Should return confluence server provider for 'confluence-server'")
        void shouldReturnServerProviderForConfluenceServer() {
            when(appProperties.getProvider()).thenReturn("confluence-server");
            factory = new ProviderFactory(appProperties, stubProvider, serverProvider);

            BaseProvider result = factory.getProvider();

            assertSame(serverProvider, result);
        }

        @Test
        @DisplayName("Should return stub provider for 'stub'")
        void shouldReturnStubProviderForStub() {
            when(appProperties.getProvider()).thenReturn("stub");
            factory = new ProviderFactory(appProperties, stubProvider, serverProvider);

            BaseProvider result = factory.getProvider();

            assertSame(stubProvider, result);
        }

        @Test
        @DisplayName("Should return stub provider for 'confluence-stub'")
        void shouldReturnStubProviderForConfluenceStub() {
            when(appProperties.getProvider()).thenReturn("confluence-stub");
            factory = new ProviderFactory(appProperties, stubProvider, serverProvider);

            BaseProvider result = factory.getProvider();

            assertSame(stubProvider, result);
        }

        @Test
        @DisplayName("Should return stub provider for unknown provider")
        void shouldReturnStubProviderForUnknown() {
            when(appProperties.getProvider()).thenReturn("unknown");
            factory = new ProviderFactory(appProperties, stubProvider, serverProvider);

            BaseProvider result = factory.getProvider();

            assertSame(stubProvider, result);
        }

        @Test
        @DisplayName("Should be case insensitive for provider name")
        void shouldBeCaseInsensitive() {
            when(appProperties.getProvider()).thenReturn("CONFLUENCE");
            factory = new ProviderFactory(appProperties, stubProvider, serverProvider);

            BaseProvider result = factory.getProvider();

            assertSame(serverProvider, result);
        }
    }

    @Nested
    @DisplayName("Stub Provider Tests")
    class StubProviderTests {

        private ConfluenceStubProvider stubProvider;

        @BeforeEach
        void setUp() {
            stubProvider = new ConfluenceStubProvider();
        }

        @Test
        @DisplayName("Should return generated page ID on publish")
        void shouldReturnGeneratedPageIdOnPublish() {
            BaseProvider.ProviderResult result = stubProvider.publishPage(
                    "TEST",
                    "Test Page",
                    "<p>Content</p>",
                    null,
                    Collections.emptyList()
            );

            assertNotNull(result);
            assertNotNull(result.confluencePageId());
            assertTrue(result.confluencePageId().startsWith("CONF-"));
            assertTrue(result.message().contains("stub"));
        }

        @Test
        @DisplayName("Should return published status")
        void shouldReturnPublishedStatus() {
            String status = stubProvider.getStatus("CONF-12345678");

            assertEquals("published", status);
        }

        @Test
        @DisplayName("Should handle attachments without error")
        void shouldHandleAttachmentsWithoutError() {
            List<String> attachments = List.of("file1.pdf", "file2.png");

            BaseProvider.ProviderResult result = stubProvider.publishPage(
                    "TEST",
                    "Test Page",
                    "<p>Content</p>",
                    123L,
                    attachments
            );

            assertNotNull(result);
            assertNotNull(result.confluencePageId());
        }
    }
}
