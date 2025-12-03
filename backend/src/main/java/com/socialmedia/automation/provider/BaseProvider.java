package com.socialmedia.automation.provider;

import java.util.List;

public interface BaseProvider {
    
    ProviderResult publish(String text, List<String> mediaPaths);
    
    record ProviderResult(String externalId, String message) {}
}

