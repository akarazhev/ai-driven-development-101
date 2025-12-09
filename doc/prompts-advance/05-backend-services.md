# Prompt 05: Backend Services

## Context
Continue building the Confluence Publisher application. Create the service layer that contains the business logic.

## Requirements

### Package Structure
All services should be in `com.confluence.publisher.service` package.

### Service: PageService
Create `service/PageService.java`:
```java
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
        
        final Long savedPageId = page.getId();
        List<PageAttachment> pageAttachmentList = IntStream.range(0, attachmentIds.size())
                .mapToObj(i -> PageAttachment.builder()
                        .pageId(savedPageId)
                        .attachmentId(attachmentIds.get(i))
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
```

### Service: AttachmentService
Create `service/AttachmentService.java`:
```java
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
    
    private final AttachmentRepository attachmentRepository;
    private final AppProperties appProperties;
    
    @Transactional
    public Attachment uploadAttachment(MultipartFile file, String description) {
        try {
            Path attachmentDir = Paths.get(appProperties.getAttachmentDir());
            Files.createDirectories(attachmentDir);
            
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID().toString().replace("-", "") + suffix;
            Path filePath = attachmentDir.resolve(filename);
            
            Files.write(filePath, file.getBytes());
            
            Attachment attachment = Attachment.builder()
                    .filename(originalFilename != null ? originalFilename : "unknown")
                    .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .size(file.getSize())
                    .storagePath(filePath.toString())
                    .description(description)
                    .build();
            
            return attachmentRepository.save(attachment);
        } catch (IOException e) {
            log.error("Failed to upload attachment", e);
            throw new RuntimeException("Failed to upload attachment: " + e.getMessage(), e);
        }
    }
}
```

### Service: ScheduleService
Create `service/ScheduleService.java`:
```java
package com.confluence.publisher.service;

import com.confluence.publisher.entity.Schedule;
import com.confluence.publisher.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    
    @Transactional
    public Schedule createSchedule(Long pageId, Instant scheduledAt) {
        Instant when = scheduledAt != null ? scheduledAt : Instant.now();
        Schedule schedule = Schedule.builder()
                .pageId(pageId)
                .scheduledAt(when)
                .status("queued")
                .build();
        return scheduleRepository.save(schedule);
    }
    
    @Transactional(readOnly = true)
    public Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + scheduleId));
    }
    
    @Transactional(readOnly = true)
    public List<Schedule> listSchedules(int limit) {
        return scheduleRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"))
        ).toList();
    }
    
    @Transactional(readOnly = true)
    public List<Schedule> findQueuedSchedules(Instant now) {
        return scheduleRepository.findQueuedSchedulesBefore(now);
    }
    
    @Transactional
    public void updateScheduleStatus(Schedule schedule, String status, String error) {
        schedule.setStatus(status);
        schedule.setAttemptCount(schedule.getAttemptCount() + 1);
        schedule.setLastError(error);
        scheduleRepository.save(schedule);
    }
}
```

### Service: PublishService
Create `service/PublishService.java`:
```java
package com.confluence.publisher.service;

import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.entity.PageAttachment;
import com.confluence.publisher.entity.PublishLog;
import com.confluence.publisher.provider.BaseProvider;
import com.confluence.publisher.provider.ProviderFactory;
import com.confluence.publisher.repository.AttachmentRepository;
import com.confluence.publisher.repository.PageAttachmentRepository;
import com.confluence.publisher.repository.PageRepository;
import com.confluence.publisher.repository.PublishLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublishService {
    
    private final PageRepository pageRepository;
    private final PageAttachmentRepository pageAttachmentRepository;
    private final AttachmentRepository attachmentRepository;
    private final PublishLogRepository publishLogRepository;
    private final ProviderFactory providerFactory;
    
    @Transactional
    public PublishLog publishPage(Long pageId) {
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found: " + pageId));
        
        List<PageAttachment> pageAttachmentList = pageAttachmentRepository.findByPageIdOrderByPosition(pageId);
        List<String> attachmentPaths = pageAttachmentList.stream()
                .map(pa -> {
                    Attachment attachment = attachmentRepository.findById(pa.getAttachmentId())
                            .orElse(null);
                    return attachment != null ? attachment.getStoragePath() : null;
                })
                .filter(path -> path != null)
                .collect(Collectors.toList());
        
        BaseProvider provider = providerFactory.getProvider();
        BaseProvider.ProviderResult result = provider.publishPage(
            page.getSpaceKey(),
            page.getTitle(),
            page.getContent(),
            page.getParentPageId(),
            attachmentPaths
        );
        
        PublishLog publishLog = PublishLog.builder()
                .pageId(pageId)
                .provider(providerFactory.getProviderName())
                .spaceKey(page.getSpaceKey())
                .confluencePageId(result.confluencePageId())
                .status("published")
                .message(result.message())
                .build();
        
        return publishLogRepository.save(publishLog);
    }
}
```

## Key Design Decisions
1. **@Transactional**: All write operations are transactional, read operations use `readOnly = true`
2. **Constructor injection**: Using `@RequiredArgsConstructor` for clean dependency injection
3. **File storage**: Attachments stored with UUID filenames to avoid conflicts
4. **Provider pattern**: PublishService uses ProviderFactory to get the appropriate Confluence provider
5. **Error handling**: RuntimeExceptions thrown for not-found cases (handled by GlobalExceptionHandler)
6. **Pagination**: ScheduleService uses Spring Data pagination for listing schedules

## Service Responsibilities
| Service | Responsibility |
|---------|---------------|
| `PageService` | Create pages, link attachments, retrieve page details |
| `AttachmentService` | Upload files to disk, create attachment records |
| `ScheduleService` | Create/manage publication schedules, find queued items |
| `PublishService` | Orchestrate publishing to Confluence via provider |

## Verification
- All services compile without errors
- Services can be injected into controllers
- Transactions work correctly (rollback on error)
- File uploads are saved to the configured directory
