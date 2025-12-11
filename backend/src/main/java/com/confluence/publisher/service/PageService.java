package com.confluence.publisher.service;

import com.confluence.publisher.dto.PageResponse;
import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.entity.PageAttachment;
import com.confluence.publisher.repository.AttachmentRepository;
import com.confluence.publisher.repository.PageAttachmentRepository;
import com.confluence.publisher.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
        Page page = Page.builder()
                .title(title)
                .content(content)
                .spaceKey(spaceKey)
                .parentPageId(parentPageId)
                .build();
        page = pageRepository.save(page);
        
        List<Long> safeAttachmentIds = attachmentIds != null ? attachmentIds : Collections.emptyList();
        final Long savedPageId = page.getId();
        List<PageAttachment> pageAttachmentList = IntStream.range(0, safeAttachmentIds.size())
                .mapToObj(i -> PageAttachment.builder()
                        .pageId(savedPageId)
                        .attachmentId(safeAttachmentIds.get(i))
                        .position(i)
                        .build())
                .toList();
        
        pageAttachmentRepository.saveAll(pageAttachmentList);
        return page;
    }
    
    @Transactional(readOnly = true)
    public PageResponse getPage(Long pageId) {
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found: " + pageId));
        
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

