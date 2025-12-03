package com.confluence.publisher.controller;

import com.confluence.publisher.dto.ConfluencePublishRequest;
import com.confluence.publisher.dto.PublishResponse;
import com.confluence.publisher.entity.PublishLog;
import com.confluence.publisher.service.PublishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/confluence")
@RequiredArgsConstructor
public class ConfluenceController {
    
    private final PublishService publishService;
    
    @PostMapping("/{accountId}/publish")
    public ResponseEntity<PublishResponse> publishNow(
            @PathVariable String accountId,
            @Valid @RequestBody ConfluencePublishRequest request) {
        
        PublishLog log = publishService.publishPost(request.getPostId());
        PublishResponse response = PublishResponse.builder()
                .logId(log.getId())
                .status(log.getStatus())
                .externalId(log.getExternalId())
                .build();
        return ResponseEntity.ok(response);
    }
}

