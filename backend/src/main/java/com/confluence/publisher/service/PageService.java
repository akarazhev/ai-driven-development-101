package com.confluence.publisher.service;

import com.confluence.publisher.dto.PageResponse;
import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.entity.PageAttachment;
import com.confluence.publisher.exception.ResourceNotFoundException;
import com.confluence.publisher.exception.ValidationException;
import com.confluence.publisher.repository.AttachmentRepository;
import com.confluence.publisher.repository.PageAttachmentRepository;
import com.confluence.publisher.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PageService {
    
    private final PageRepository pageRepository;
    private final PageAttachmentRepository pageAttachmentRepository;
    private final AttachmentRepository attachmentRepository;
    
    @Transactional
    public Page createPage(String title, String content, String spaceKey, Long parentPageId, List<Long> attachmentIds) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Page title is required");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new ValidationException("Page content is required");
        }
        if (spaceKey == null || spaceKey.trim().isEmpty()) {
            throw new ValidationException("Space key is required");
        }
        
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            for (Long attachmentId : attachmentIds) {
                if (!attachmentRepository.existsById(attachmentId)) {
                    throw new ResourceNotFoundException("Attachment", attachmentId);
                }
            }
        }
        
        Page page = Page.builder()
                .title(title.trim())
                .content(content.trim())
                .spaceKey(spaceKey.trim())
                .parentPageId(parentPageId)
                .build();
        page = pageRepository.save(page);
        
        final Long savedPageId = page.getId();
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            List<PageAttachment> pageAttachmentList = IntStream.range(0, attachmentIds.size())
                    .mapToObj(i -> PageAttachment.builder()
                            .pageId(savedPageId)
                            .attachmentId(attachmentIds.get(i))
                            .position(i)
                            .build())
                    .toList();
            
            pageAttachmentRepository.saveAll(pageAttachmentList);
        }
        return page;
    }
    
    @Transactional(readOnly = true)
    public PageResponse getPage(Long pageId) {
        if (pageId == null) {
            throw new ValidationException("Page ID is required");
        }
        
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new ResourceNotFoundException("Page", pageId));
        
        List<PageAttachment> pageAttachmentList = pageAttachmentRepository.findByPageIdOrderByPosition(pageId);
        List<PageResponse.AttachmentInfo> attachments = pageAttachmentList.stream()
                .map(pa -> {
                    Attachment attachment = attachmentRepository.findById(pa.getAttachmentId()).orElse(null);
                    if (attachment != null) {
                        return PageResponse.AttachmentInfo.builder()
                                .id(attachment.getId())
                                .filename(attachment.getFilename())
                                .description(attachment.getDescription())
                                .build();
                    }
                    return null;
                })
                .filter(a -> a != null)
                .toList();
        
        return PageResponse.builder()
                .id(page.getId())
                .title(page.getTitle())
                .content(page.getContent())
                .spaceKey(page.getSpaceKey())
                .parentPageId(page.getParentPageId())
                .attachments(attachments)
                .build();
    }
}

