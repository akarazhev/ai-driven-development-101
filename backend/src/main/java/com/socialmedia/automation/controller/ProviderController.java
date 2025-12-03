package com.socialmedia.automation.controller;

import com.socialmedia.automation.dto.PublishRequest;
import com.socialmedia.automation.dto.PublishResponse;
import com.socialmedia.automation.entity.PublishLog;
import com.socialmedia.automation.service.PublishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {
    
    private final PublishService publishService;
    
    @PostMapping("/{accountId}/publish")
    public ResponseEntity<PublishResponse> publishNow(
            @PathVariable String accountId,
            @Valid @RequestBody PublishRequest request) {
        
        PublishLog log = publishService.publishPost(request.getPostId());
        PublishResponse response = PublishResponse.builder()
                .logId(log.getId())
                .status(log.getStatus())
                .externalId(log.getExternalId())
                .build();
        return ResponseEntity.ok(response);
    }
}

