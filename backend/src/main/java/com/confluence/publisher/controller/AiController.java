package com.confluence.publisher.controller;

import com.confluence.publisher.dto.ContentImprovementRequest;
import com.confluence.publisher.dto.ContentImprovementResponse;
import com.confluence.publisher.dto.AttachmentDescriptionRequest;
import com.confluence.publisher.dto.AttachmentDescriptionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    
    @PostMapping("/improve-content")
    public ResponseEntity<ContentImprovementResponse> improveContent(@Valid @RequestBody ContentImprovementRequest request) {
        String base = request.getContent().strip();
        List<String> outs = new ArrayList<>();
        
        if (!base.isEmpty()) {
            outs.add(base);
            if (base.length() > 180) {
                outs.add(base.substring(0, 180) + " #update");
            }
            if (base.length() > 200) {
                outs.add(base.substring(0, 200).toUpperCase());
            } else {
                outs.add(base.toUpperCase());
            }
        }
        
        List<String> suggestions = outs.stream()
                .filter(s -> !s.isEmpty())
                .limit(3)
                .toList();
        
        ContentImprovementResponse response = ContentImprovementResponse.builder()
                .suggestions(suggestions)
                .build();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/generate-description")
    public ResponseEntity<AttachmentDescriptionResponse> generateDescription(@RequestBody AttachmentDescriptionRequest request) {
        String desc = request.getDescription() != null 
                ? request.getDescription().strip() 
                : "";
        if (desc.isEmpty()) {
            desc = "Document attachment";
        }
        String description = desc.length() > 120 ? desc.substring(0, 120) : desc;
        
        AttachmentDescriptionResponse response = AttachmentDescriptionResponse.builder()
                .description(description)
                .build();
        return ResponseEntity.ok(response);
    }
}

