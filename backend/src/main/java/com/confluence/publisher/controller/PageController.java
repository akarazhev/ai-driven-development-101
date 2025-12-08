package com.confluence.publisher.controller;

import com.confluence.publisher.dto.PageCreateRequest;
import com.confluence.publisher.dto.PageResponse;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.service.PageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
public class PageController {
    
    private final PageService pageService;
    private final com.confluence.publisher.config.AppProperties appProperties;
    
    @PostMapping
    public ResponseEntity<PageResponse> createPage(@Valid @RequestBody PageCreateRequest request) {
        // Use default space from configuration if not provided in request
        String spaceKey = request.getSpaceKey() != null && !request.getSpaceKey().isBlank() 
            ? request.getSpaceKey() 
            : appProperties.getConfluenceDefaultSpace();
        
        Page page = pageService.createPage(
            request.getTitle(), 
            request.getContent(), 
            spaceKey,
            request.getParentPageId(),
            request.getAttachmentIds()
        );
        PageResponse response = PageResponse.builder()
                .id(page.getId())
                .title(page.getTitle())
                .content(page.getContent())
                .spaceKey(page.getSpaceKey())
                .parentPageId(page.getParentPageId())
                .attachments(List.of()) // Attachments will be loaded on get
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{pageId}")
    public ResponseEntity<PageResponse> getPage(@PathVariable Long pageId) {
        PageResponse response = pageService.getPage(pageId);
        return ResponseEntity.ok(response);
    }
}

