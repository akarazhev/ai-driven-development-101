# Prompt 07: Backend REST Controllers

## Context
Continue building the Confluence Publisher application. Create the REST controllers that expose the API endpoints.

## Requirements

### Package Structure
All controllers should be in `com.confluence.publisher.controller` package.

### Controller: HealthController
Create `controller/HealthController.java`:
```java
package com.confluence.publisher.controller;

import com.confluence.publisher.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {
    
    private final AppProperties appProperties;
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
    
    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getConfig() {
        return ResponseEntity.ok(Map.of(
            "defaultSpace", appProperties.getConfluenceDefaultSpace()
        ));
    }
}
```

### Controller: PageController
Create `controller/PageController.java`:
```java
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
```

### Controller: AttachmentController
Create `controller/AttachmentController.java`:
```java
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
    
    private final AttachmentService attachmentService;
    
    @PostMapping
    public ResponseEntity<AttachmentUploadResponse> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        
        Attachment attachment = attachmentService.uploadAttachment(file, description);
        AttachmentUploadResponse response = AttachmentUploadResponse.builder()
                .id(attachment.getId())
                .filename(attachment.getFilename())
                .description(attachment.getDescription())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

### Controller: ScheduleController
Create `controller/ScheduleController.java`:
```java
package com.confluence.publisher.controller;

import com.confluence.publisher.dto.ScheduleCreateRequest;
import com.confluence.publisher.dto.ScheduleResponse;
import com.confluence.publisher.entity.Schedule;
import com.confluence.publisher.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    
    private final ScheduleService scheduleService;
    
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleCreateRequest request) {
        Schedule schedule = scheduleService.createSchedule(
                request.getPageId(),
                request.getScheduledAt()
        );
        ScheduleResponse response = toResponse(schedule);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getSchedule(@PathVariable Long scheduleId) {
        Schedule schedule = scheduleService.getSchedule(scheduleId);
        return ResponseEntity.ok(toResponse(schedule));
    }
    
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> listSchedules() {
        List<Schedule> schedules = scheduleService.listSchedules(100);
        List<ScheduleResponse> responses = schedules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    private ScheduleResponse toResponse(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .pageId(schedule.getPageId())
                .status(schedule.getStatus())
                .scheduledAt(schedule.getScheduledAt())
                .attemptCount(schedule.getAttemptCount())
                .lastError(schedule.getLastError())
                .build();
    }
}
```

### Controller: ConfluenceController
Create `controller/ConfluenceController.java`:
```java
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
    
    @PostMapping("/publish")
    public ResponseEntity<PublishResponse> publishNow(
            @Valid @RequestBody ConfluencePublishRequest request) {
        
        PublishLog publishLog = publishService.publishPage(request.getPageId());
        PublishResponse response = PublishResponse.builder()
                .logId(publishLog.getId())
                .status(publishLog.getStatus())
                .confluencePageId(publishLog.getConfluencePageId())
                .build();
        return ResponseEntity.ok(response);
    }
}
```

### Controller: AiController
Create `controller/AiController.java`:
```java
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
        // Stub implementation - generates simple content variations
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
        // Stub implementation - returns sanitized description
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
```

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Health check |
| GET | `/api/config` | Get frontend configuration (default space) |
| POST | `/api/pages` | Create a new page |
| GET | `/api/pages/{pageId}` | Get page by ID |
| POST | `/api/attachments` | Upload attachment (multipart) |
| POST | `/api/schedules` | Create publication schedule |
| GET | `/api/schedules/{scheduleId}` | Get schedule by ID |
| GET | `/api/schedules` | List all schedules |
| POST | `/api/confluence/publish` | Publish page immediately |
| POST | `/api/ai/improve-content` | Get content suggestions |
| POST | `/api/ai/generate-description` | Generate attachment description |

## Key Design Decisions
1. **RESTful design**: Standard HTTP methods and status codes
2. **Validation**: Using `@Valid` with Jakarta Validation annotations
3. **Response mapping**: Controllers map entities to DTOs before returning
4. **Default space**: PageController uses configured default space if not provided
5. **Multipart upload**: AttachmentController handles file uploads via `@RequestParam`
6. **AI stubs**: AiController provides stub implementations for content improvement

## Verification
- All endpoints respond correctly
- Validation errors return 400 Bad Request
- Not found errors return 404
- CORS headers are present for frontend access
- File uploads work with multipart/form-data
