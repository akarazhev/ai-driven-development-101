package com.confluence.publisher.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class ConfluenceStubProvider implements BaseProvider {
    
    @Override
    public ProviderResult publishPage(
        String spaceKey,
        String title,
        String content,
        Long parentPageId,
        List<String> attachmentPaths
    ) {
        String pageId = "CONF-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("Stub: Publishing page '{}' to Confluence space '{}' (parent: {})", 
                 title, spaceKey, parentPageId);
        log.info("Stub: Page ID: {}, Attachments: {}", pageId, attachmentPaths.size());
        return new ProviderResult(pageId, "Successfully published to Confluence (stub)");
    }
    
    @Override
    public String getStatus(String confluencePageId) {
        log.info("Stub: Getting status for Confluence page {}", confluencePageId);
        return "published";
    }
}

