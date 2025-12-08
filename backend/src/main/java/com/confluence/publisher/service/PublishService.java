package com.confluence.publisher.service;

import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.entity.PageAttachment;
import com.confluence.publisher.entity.PublishLog;
import com.confluence.publisher.exception.ResourceNotFoundException;
import com.confluence.publisher.exception.ServiceException;
import com.confluence.publisher.exception.ValidationException;
import com.confluence.publisher.provider.BaseProvider;
import com.confluence.publisher.provider.ProviderFactory;
import com.confluence.publisher.repository.AttachmentRepository;
import com.confluence.publisher.repository.PageAttachmentRepository;
import com.confluence.publisher.repository.PageRepository;
import com.confluence.publisher.repository.PublishLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublishService {
    
    private final PageRepository pageRepository;
    private final PageAttachmentRepository pageAttachmentRepository;
    private final AttachmentRepository attachmentRepository;
    private final PublishLogRepository publishLogRepository;
    private final ProviderFactory providerFactory;
    
    @Transactional
    public PublishLog publishPage(Long pageId) {
        if (pageId == null) {
            throw new ValidationException("Page ID is required");
        }
        
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new ResourceNotFoundException("Page", pageId));
        
        try {
            List<PageAttachment> pageAttachmentList = pageAttachmentRepository.findByPageIdOrderByPosition(pageId);
            List<String> attachmentPaths = pageAttachmentList.stream()
                    .map(pa -> {
                        Attachment attachment = attachmentRepository.findById(pa.getAttachmentId())
                                .orElse(null);
                        return attachment != null ? attachment.getStoragePath() : null;
                    })
                    .filter(path -> path != null)
                    .collect(Collectors.toList());
            
            BaseProvider provider = providerFactory.getProvider();
            BaseProvider.ProviderResult result = provider.publishPage(
                page.getSpaceKey(),
                page.getTitle(),
                page.getContent(),
                page.getParentPageId(),
                attachmentPaths
            );
            
            PublishLog publishLog = PublishLog.builder()
                    .pageId(pageId)
                    .provider(providerFactory.getProviderName())
                    .spaceKey(page.getSpaceKey())
                    .confluencePageId(result.confluencePageId())
                    .status("published")
                    .message(result.message())
                    .build();
            
            PublishLog savedLog = publishLogRepository.save(publishLog);
            return savedLog;
        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to publish page {}: {}", pageId, e.getMessage(), e);
            throw new ServiceException("Failed to publish page: " + e.getMessage(), e);
        }
    }
}

