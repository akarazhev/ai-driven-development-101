package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProviderFactory {
    
    private final AppProperties appProperties;
    private final ConfluenceStubProvider stubProvider;
    private final ConfluenceApiProvider apiProvider;
    
    public BaseProvider getProvider() {
        String providerName = appProperties.getProvider().toLowerCase();
        return switch (providerName) {
            case "stub", "confluence-stub" -> stubProvider;
            case "api", "confluence-api", "confluence" -> apiProvider;
            default -> {
                // Default to stub if provider name is not recognized
                if (apiProvider != null && 
                    appProperties.getConfluenceUrl() != null && 
                    !appProperties.getConfluenceUrl().isEmpty() &&
                    !appProperties.getConfluenceUrl().contains("your-domain")) {
                    yield apiProvider;
                }
                yield stubProvider;
            }
        };
    }
    
    public String getProviderName() {
        return appProperties.getProvider();
    }
}

