package com.confluence.publisher.controller;

import com.confluence.publisher.dto.AttachmentUploadResponse;
import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    
    private final AttachmentService mediaService;
    
    @PostMapping
    public ResponseEntity<AttachmentUploadResponse> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "alt_text", required = false) String altText) {
        
        Attachment asset = mediaService.uploadAttachment(file, altText);
        AttachmentUploadResponse response = AttachmentUploadResponse.builder()
                .id(asset.getId())
                .filename(asset.getFilename())
                .altText(asset.getAltText())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

