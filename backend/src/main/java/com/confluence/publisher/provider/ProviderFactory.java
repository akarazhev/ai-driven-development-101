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

