package com.socialmedia.automation.controller;

import com.socialmedia.automation.dto.MediaUploadResponse;
import com.socialmedia.automation.entity.MediaAsset;
import com.socialmedia.automation.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {
    
    private final MediaService mediaService;
    
    @PostMapping
    public ResponseEntity<MediaUploadResponse> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "alt_text", required = false) String altText) {
        
        MediaAsset asset = mediaService.uploadMedia(file, altText);
        MediaUploadResponse response = MediaUploadResponse.builder()
                .id(asset.getId())
                .filename(asset.getFilename())
                .altText(asset.getAltText())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

