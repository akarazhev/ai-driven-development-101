package com.confluence.publisher.service;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.exception.ServiceException;
import com.confluence.publisher.exception.ValidationException;
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
    
    private final AttachmentRepository attachmentRepository;
    private final AppProperties appProperties;
    
    @Transactional
    public Attachment uploadAttachment(MultipartFile file, String description) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File is required");
        }
        
        if (file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
            throw new ValidationException("File name is required");
        }
        
        // Check file size (e.g., max 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new ValidationException("File size exceeds maximum allowed size of 10MB");
        }
        
        try {
            Path attachmentDir = Paths.get(appProperties.getAttachmentDir());
            Files.createDirectories(attachmentDir);
            
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID().toString().replace("-", "") + suffix;
            Path filePath = attachmentDir.resolve(filename);
            
            Files.write(filePath, file.getBytes());
            
            Attachment attachment = Attachment.builder()
                    .filename(originalFilename)
                    .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .size(file.getSize())
                    .storagePath(filePath.toString())
                    .description(description != null ? description.trim() : null)
                    .build();
            
            return attachmentRepository.save(attachment);
        } catch (IOException e) {
            log.error("Failed to upload attachment: {}", e.getMessage(), e);
            throw new ServiceException("Failed to upload attachment: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during attachment upload: {}", e.getMessage(), e);
            throw new ServiceException("An unexpected error occurred while uploading the attachment", e);
        }
    }
}

