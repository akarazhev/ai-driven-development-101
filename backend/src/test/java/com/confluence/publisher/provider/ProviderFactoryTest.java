package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProviderFactoryTest {

    @Mock
    private AppProperties appProperties;

    @Mock
    private ConfluenceStubProvider stubProvider;

    @Mock
    private ConfluenceServerProvider confluenceServerProvider;

    private ProviderFactory providerFactory;

    @BeforeEach
    void setUp() {
        providerFactory = new ProviderFactory(appProperties, stubProvider, confluenceServerProvider);
    }

    @Test
    @DisplayName("should return ConfluenceServerProvider for 'confluence' provider name")
    void shouldReturnConfluenceServerProviderForConfluence() {
        when(appProperties.getProvider()).thenReturn("confluence");

        BaseProvider provider = providerFactory.getProvider();

        assertSame(confluenceServerProvider, provider);
    }

    @Test
    @DisplayName("should return ConfluenceServerProvider for 'confluence-server' provider name")
    void shouldReturnConfluenceServerProviderForConfluenceServer() {
        when(appProperties.getProvider()).thenReturn("confluence-server");

        BaseProvider provider = providerFactory.getProvider();

        assertSame(confluenceServerProvider, provider);
    }

    @Test
    @DisplayName("should return ConfluenceServerProvider for uppercase 'CONFLUENCE' provider name")
    void shouldReturnConfluenceServerProviderForUppercaseConfluence() {
        when(appProperties.getProvider()).thenReturn("CONFLUENCE");

        BaseProvider provider = providerFactory.getProvider();

        assertSame(confluenceServerProvider, provider);
    }

    @Test
    @DisplayName("should return stub provider for 'stub' provider name")
    void shouldReturnStubProviderForStub() {
        when(appProperties.getProvider()).thenReturn("stub");

        BaseProvider provider = providerFactory.getProvider();

        assertSame(stubProvider, provider);
    }

    @Test
    @DisplayName("should return stub provider for 'confluence-stub' provider name")
    void shouldReturnStubProviderForConfluenceStub() {
        when(appProperties.getProvider()).thenReturn("confluence-stub");

        BaseProvider provider = providerFactory.getProvider();

        assertSame(stubProvider, provider);
    }

    @Test
    @DisplayName("should return stub provider for unknown provider name")
    void shouldReturnStubProviderForUnknown() {
        when(appProperties.getProvider()).thenReturn("unknown-provider");

        BaseProvider provider = providerFactory.getProvider();

        assertSame(stubProvider, provider);
    }

    @Test
    @DisplayName("should return stub provider for empty provider name")
    void shouldReturnStubProviderForEmpty() {
        when(appProperties.getProvider()).thenReturn("");

        BaseProvider provider = providerFactory.getProvider();

        assertSame(stubProvider, provider);
    }

    @Test
    @DisplayName("should return provider name from properties")
    void shouldReturnProviderName() {
        when(appProperties.getProvider()).thenReturn("confluence");

        String providerName = providerFactory.getProviderName();

        assertEquals("confluence", providerName);
    }
}
