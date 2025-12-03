package com.socialmedia.automation.provider;

import com.socialmedia.automation.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProviderFactory {
    
    private final AppProperties appProperties;
    private final StubProvider stubProvider;
    
    public BaseProvider getProvider() {
        String providerName = appProperties.getProvider().toLowerCase();
        return switch (providerName) {
            case "stub" -> stubProvider;
            default -> stubProvider;
        };
    }
    
    public String getProviderName() {
        return appProperties.getProvider();
    }
}

