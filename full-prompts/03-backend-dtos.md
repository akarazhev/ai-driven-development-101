# Prompt 03: Backend DTOs (Data Transfer Objects)

## Context
Continue building the Confluence Publisher application. Create the DTOs for API request/response handling.

## Requirements

### Package Structure
All DTOs should be in `com.confluence.publisher.dto` package.

### DTO: PageCreateRequest
Create `dto/PageCreateRequest.java`:
```java
package com.confluence.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageCreateRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    // Space key is optional - will use default from configuration if not provided
    private String spaceKey;
    
    private Long parentPageId;
    
    @NotNull
    private List<Long> attachmentIds = new ArrayList<>();
}
```

### DTO: PageResponse
Create `dto/PageResponse.java`:
```java
package com.confluence.publisher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse {
    
    private Long id;
    private String title;
    private String content;
    private String spaceKey;
    private Long parentPageId;
    private List<AttachmentInfo> attachments = new ArrayList<>();
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentInfo {
        private Long id;
        private String filename;
        private String description;
    }
}
```

### DTO: AttachmentUploadResponse
Create `dto/AttachmentUploadResponse.java`:
```java
package com.confluence.publisher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentUploadResponse {
    
    private Long id;
    private String filename;
    private String description;
}
```

### DTO: ScheduleCreateRequest
Create `dto/ScheduleCreateRequest.java`:
```java
package com.confluence.publisher.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class ScheduleCreateRequest {
    
    @NotNull(message = "Page ID is required")
    private Long pageId;
    
    private Instant scheduledAt;  // Optional - defaults to now if not provided
}
```

### DTO: ScheduleResponse
Create `dto/ScheduleResponse.java`:
```java
package com.confluence.publisher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    
    private Long id;
    private Long pageId;
    private String status;
    private Instant scheduledAt;
    private Integer attemptCount;
    private String lastError;
}
```

### DTO: ConfluencePublishRequest
Create `dto/ConfluencePublishRequest.java`:
```java
package com.confluence.publisher.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfluencePublishRequest {
    
    @NotNull(message = "Page ID is required")
    private Long pageId;
}
```

### DTO: PublishResponse
Create `dto/PublishResponse.java`:
```java
package com.confluence.publisher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishResponse {
    
    private Long logId;
    private String status;
    private String confluencePageId;
}
```

### DTO: ContentImprovementRequest
Create `dto/ContentImprovementRequest.java`:
```java
package com.confluence.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContentImprovementRequest {
    
    @NotBlank(message = "Content is required")
    private String content;
}
```

### DTO: ContentImprovementResponse
Create `dto/ContentImprovementResponse.java`:
```java
package com.confluence.publisher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentImprovementResponse {
    
    private List<String> suggestions;
}
```

### DTO: AttachmentDescriptionRequest
Create `dto/AttachmentDescriptionRequest.java`:
```java
package com.confluence.publisher.dto;

import lombok.Data;

@Data
public class AttachmentDescriptionRequest {
    
    private String description;
}
```

### DTO: AttachmentDescriptionResponse
Create `dto/AttachmentDescriptionResponse.java`:
```java
package com.confluence.publisher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDescriptionResponse {
    
    private String description;
}
```

## Key Design Decisions
1. **Validation annotations**: Using Jakarta Validation (`@NotBlank`, `@NotNull`) for input validation
2. **Builder pattern**: Response DTOs use `@Builder` for clean construction in services
3. **Nested classes**: `PageResponse.AttachmentInfo` is a nested static class for attachment metadata
4. **Optional fields**: Some fields like `spaceKey` and `scheduledAt` are optional with defaults handled in service layer
5. **Separation of concerns**: Request DTOs are simple POJOs, Response DTOs have builders

## Verification
- All DTOs compile without errors
- Validation annotations are properly applied
- DTOs can be serialized/deserialized by Jackson
