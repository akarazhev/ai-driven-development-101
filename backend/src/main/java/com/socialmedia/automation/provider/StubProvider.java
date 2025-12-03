package com.socialmedia.automation.provider;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class StubProvider implements BaseProvider {
    
    @Override
    public ProviderResult publish(String text, List<String> mediaPaths) {
        return new ProviderResult(UUID.randomUUID().toString(), "ok");
    }
}

