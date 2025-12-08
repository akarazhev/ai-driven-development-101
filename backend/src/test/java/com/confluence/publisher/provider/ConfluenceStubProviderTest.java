package com.confluence.publisher.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ConfluenceStubProviderTest {

    private ConfluenceStubProvider provider;

    @BeforeEach
    void setUp() {
        provider = new ConfluenceStubProvider();
    }

    @Test
    void shouldPublishPageSuccessfully() {
        var result = provider.publishPage(
                "DEV",
                "Test Page",
                "Test Content",
                null,
                Collections.emptyList()
        );

        assertNotNull(result);
        assertNotNull(result.confluencePageId());
        assertNotNull(result.message());
        assertTrue(result.confluencePageId().startsWith("CONF-"));
    }

    @Test
    void shouldReturnStatusForPublishedPage() {
        var publishResult = provider.publishPage(
                "DEV",
                "Test Page",
                "Test Content",
                null,
                Collections.emptyList()
        );

        var status = provider.getStatus(publishResult.confluencePageId());

        assertNotNull(status);
        assertTrue(status.equals("published") || status.equals("pending"));
    }
}

