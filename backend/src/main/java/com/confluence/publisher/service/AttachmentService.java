package com.confluence.publisher.service;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentService {
    
    private final AttachmentRepository mediaAssetRepository;
    private final AppProperties appProperties;
    
    @Transactional
    public Attachment uploadMedia(MultipartFile file, String description) {
        try {
            Path mediaDir = Paths.get(appProperties.getMediaDir());
            Files.createDirectories(mediaDir);
            
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID().toString().replace("-", "") + suffix;
            Path filePath = mediaDir.resolve(filename);
            
            Files.write(filePath, file.getBytes());
            
            Attachment asset = MediaAsset.builder()
                    .filename(originalFilename != null ? originalFilename : "unknown")
                    .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .size(file.getSize())
                    .storagePath(filePath.toString())
                    .description(description)
                    .build();
            
            return mediaAssetRepository.save(asset);
        } catch (IOException e) {
            log.error("Failed to upload media", e);
            throw new RuntimeException("Failed to upload media: " + e.getMessage(), e);
        }
    }
}

