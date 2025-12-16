package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.exception.ConfluenceApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfluenceServerProviderTest {

    private WireMockServer wireMockServer;
    private ConfluenceServerProvider provider;
    private AppProperties appProperties;
    private ObjectMapper objectMapper;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        appProperties = new AppProperties();
        appProperties.setConfluenceUrl("http://localhost:" + wireMockServer.port() + "/confluence");
        appProperties.setConfluenceApiToken("test-token");
        appProperties.setConfluenceUsername("test-user");
        appProperties.setConfluenceDefaultSpace("TEST");
        appProperties.setAttachmentDir(tempDir.toString());

        objectMapper = new ObjectMapper();
        provider = new ConfluenceServerProvider(appProperties, objectMapper);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Nested
    @DisplayName("publishPage tests")
    class PublishPageTests {

        @Test
        @DisplayName("Should create new page when page does not exist")
        void shouldCreateNewPageWhenNotExists() {
            stubFor(get(urlPathEqualTo("/confluence/rest/api/content"))
                    .withQueryParam("spaceKey", equalTo("TEST"))
                    .withQueryParam("title", equalTo("Test Page"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"results\": []}")));

            stubFor(post(urlPathEqualTo("/confluence/rest/api/content"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"id\": \"12345\", \"title\": \"Test Page\"}")));

            BaseProvider.ProviderResult result = provider.publishPage(
                    "TEST", "Test Page", "<p>Content</p>", null, Collections.emptyList());

            assertNotNull(result);
            assertEquals("12345", result.confluencePageId());
            assertTrue(result.message().contains("Successfully published"));

            verify(postRequestedFor(urlPathEqualTo("/confluence/rest/api/content"))
                    .withHeader("Authorization", equalTo("Bearer test-token"))
                    .withHeader("Content-Type", equalTo("application/json")));
        }

        @Test
        @DisplayName("Should update existing page when page exists")
        void shouldUpdateExistingPage() {
            stubFor(get(urlPathEqualTo("/confluence/rest/api/content"))
                    .withQueryParam("spaceKey", equalTo("TEST"))
                    .withQueryParam("title", equalTo("Existing Page"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"results\": [{\"id\": \"67890\", \"title\": \"Existing Page\", \"version\": {\"number\": 5}}]}")));

            stubFor(put(urlPathEqualTo("/confluence/rest/api/content/67890"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"id\": \"67890\", \"title\": \"Existing Page\", \"version\": {\"number\": 6}}")));

            BaseProvider.ProviderResult result = provider.publishPage(
                    "TEST", "Existing Page", "<p>Updated Content</p>", null, Collections.emptyList());

            assertNotNull(result);
            assertEquals("67890", result.confluencePageId());

            verify(putRequestedFor(urlPathEqualTo("/confluence/rest/api/content/67890"))
                    .withRequestBody(containing("\"number\":6")));
        }

        @Test
        @DisplayName("Should create page with parent when parentPageId is provided")
        void shouldCreatePageWithParent() {
            stubFor(get(urlPathEqualTo("/confluence/rest/api/content"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"results\": []}")));

            stubFor(post(urlPathEqualTo("/confluence/rest/api/content"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"id\": \"11111\", \"title\": \"Child Page\"}")));

            BaseProvider.ProviderResult result = provider.publishPage(
                    "TEST", "Child Page", "<p>Child Content</p>", 99999L, Collections.emptyList());

            assertNotNull(result);
            assertEquals("11111", result.confluencePageId());

            verify(postRequestedFor(urlPathEqualTo("/confluence/rest/api/content"))
                    .withRequestBody(containing("\"ancestors\"")));
        }

        @Test
        @DisplayName("Should upload attachments after page creation")
        void shouldUploadAttachments() throws IOException {
            Path attachmentFile = tempDir.resolve("test-attachment.txt");
            Files.writeString(attachmentFile, "Test attachment content");

            stubFor(get(urlPathEqualTo("/confluence/rest/api/content"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"results\": []}")));

            stubFor(post(urlPathEqualTo("/confluence/rest/api/content"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"id\": \"22222\", \"title\": \"Page With Attachment\"}")));

            stubFor(post(urlPathEqualTo("/confluence/rest/api/content/22222/child/attachment"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"results\": [{\"id\": \"att123\"}]}")));

            BaseProvider.ProviderResult result = provider.publishPage(
                    "TEST", "Page With Attachment", "<p>Content</p>", null,
                    List.of(attachmentFile.toString()));

            assertNotNull(result);
            assertEquals("22222", result.confluencePageId());

            verify(postRequestedFor(urlPathEqualTo("/confluence/rest/api/content/22222/child/attachment"))
                    .withHeader("X-Atlassian-Token", equalTo("nocheck")));
        }

        @Test
        @DisplayName("Should throw ConfluenceApiException on 401 Unauthorized")
        void shouldThrowExceptionOnUnauthorized() {
            stubFor(get(urlPathEqualTo("/confluence/rest/api/content"))
                    .willReturn(aResponse()
                            .withStatus(401)
                            .withBody("Unauthorized")));

            ConfluenceApiException exception = assertThrows(ConfluenceApiException.class, () ->
                    provider.publishPage("TEST", "Test Page", "<p>Content</p>", null, Collections.emptyList()));

            assertTrue(exception.isUnauthorized());
            assertEquals(401, exception.getStatusCode());
        }

        @Test
        @DisplayName("Should throw ConfluenceApiException on 403 Forbidden")
        void shouldThrowExceptionOnForbidden() {
            stubFor(get(urlPathEqualTo("/confluence/rest/api/content"))
                    .willReturn(aResponse()
                            .withStatus(403)
                            .withBody("Forbidden")));

            ConfluenceApiException exception = assertThrows(ConfluenceApiException.class, () ->
                    provider.publishPage("TEST", "Test Page", "<p>Content</p>", null, Collections.emptyList()));

            assertTrue(exception.isForbidden());
            assertEquals(403, exception.getStatusCode());
        }
    }

    @Nested
    @DisplayName("getStatus tests")
    class GetStatusTests {

        @Test
        @DisplayName("Should return 'published' when page exists")
        void shouldReturnPublishedWhenPageExists() {
            stubFor(get(urlPathEqualTo("/confluence/rest/api/content/12345"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"id\": \"12345\", \"title\": \"Test Page\", \"version\": {\"number\": 1}}")));

            String status = provider.getStatus("12345");

            assertEquals("published", status);
        }

        @Test
        @DisplayName("Should return 'not_found' when page does not exist")
        void shouldReturnNotFoundWhenPageDoesNotExist() {
            stubFor(get(urlPathEqualTo("/confluence/rest/api/content/99999"))
                    .willReturn(aResponse()
                            .withStatus(404)
                            .withBody("Not Found")));

            String status = provider.getStatus("99999");

            assertEquals("not_found", status);
        }

        @Test
        @DisplayName("Should return 'not_found' for empty pageId")
        void shouldReturnNotFoundForEmptyPageId() {
            String status = provider.getStatus("");

            assertEquals("not_found", status);
        }

        @Test
        @DisplayName("Should return 'not_found' for null pageId")
        void shouldReturnNotFoundForNullPageId() {
            String status = provider.getStatus(null);

            assertEquals("not_found", status);
        }

        @Test
        @DisplayName("Should return error message on API failure")
        void shouldReturnErrorOnApiFailure() {
            stubFor(get(urlPathEqualTo("/confluence/rest/api/content/12345"))
                    .willReturn(aResponse()
                            .withStatus(500)
                            .withBody("Internal Server Error")));

            String status = provider.getStatus("12345");

            assertTrue(status.startsWith("error:"));
        }
    }

    @Nested
    @DisplayName("Retry logic tests")
    class RetryLogicTests {

        @Test
        @DisplayName("Should retry on 429 rate limit error")
        void shouldRetryOnRateLimit() {
            stubFor(get(urlPathEqualTo("/confluence/rest/api/content"))
                    .inScenario("Rate Limit")
                    .whenScenarioStateIs("Started")
                    .willReturn(aResponse()
                            .withStatus(429)
                            .withBody("Rate Limited"))
                    .willSetStateTo("First Retry"));

            stubFor(get(urlPathEqualTo("/confluence/rest/api/content"))
                    .inScenario("Rate Limit")
                    .whenScenarioStateIs("First Retry")
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"results\": []}")));

            stubFor(post(urlPathEqualTo("/confluence/rest/api/content"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"id\": \"33333\", \"title\": \"Test Page\"}")));

            BaseProvider.ProviderResult result = provider.publishPage(
                    "TEST", "Test Page", "<p>Content</p>", null, Collections.emptyList());

            assertNotNull(result);
            assertEquals("33333", result.confluencePageId());
        }

        @Test
        @DisplayName("Should retry on 5xx server error")
        void shouldRetryOnServerError() {
            stubFor(get(urlPathEqualTo("/confluence/rest/api/content"))
                    .inScenario("Server Error")
                    .whenScenarioStateIs("Started")
                    .willReturn(aResponse()
                            .withStatus(503)
                            .withBody("Service Unavailable"))
                    .willSetStateTo("First Retry"));

            stubFor(get(urlPathEqualTo("/confluence/rest/api/content"))
                    .inScenario("Server Error")
                    .whenScenarioStateIs("First Retry")
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"results\": []}")));

            stubFor(post(urlPathEqualTo("/confluence/rest/api/content"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"id\": \"44444\", \"title\": \"Test Page\"}")));

            BaseProvider.ProviderResult result = provider.publishPage(
                    "TEST", "Test Page", "<p>Content</p>", null, Collections.emptyList());

            assertNotNull(result);
            assertEquals("44444", result.confluencePageId());
        }
    }

    @Nested
    @DisplayName("ConfluenceApiException tests")
    class ConfluenceApiExceptionTests {

        @Test
        @DisplayName("Should identify retryable status codes")
        void shouldIdentifyRetryableStatusCodes() {
            ConfluenceApiException rateLimited = new ConfluenceApiException("Rate limited", 429, null);
            ConfluenceApiException serverError = new ConfluenceApiException("Server error", 500, null);
            ConfluenceApiException unauthorized = new ConfluenceApiException("Unauthorized", 401, null);

            assertTrue(rateLimited.isRetryable());
            assertTrue(serverError.isRetryable());
            assertFalse(unauthorized.isRetryable());
        }

        @Test
        @DisplayName("Should identify specific error types")
        void shouldIdentifySpecificErrorTypes() {
            ConfluenceApiException unauthorized = new ConfluenceApiException("Unauthorized", 401, null);
            ConfluenceApiException forbidden = new ConfluenceApiException("Forbidden", 403, null);
            ConfluenceApiException notFound = new ConfluenceApiException("Not found", 404, null);
            ConfluenceApiException rateLimited = new ConfluenceApiException("Rate limited", 429, null);
            ConfluenceApiException serverError = new ConfluenceApiException("Server error", 502, null);

            assertTrue(unauthorized.isUnauthorized());
            assertTrue(forbidden.isForbidden());
            assertTrue(notFound.isNotFound());
            assertTrue(rateLimited.isRateLimited());
            assertTrue(serverError.isServerError());
        }
    }
}
