package com.confluence.publisher.provider;

import java.util.List;

public interface BaseProvider {
    
    ProviderResult publishPage(
        String spaceKey,
        String title,
        String content,
        Long parentPageId,
        List<String> attachmentPaths
    );
    
    String getStatus(String confluencePageId);
    
    record ProviderResult(String confluencePageId, String message) {}
}

