# Prompt 02: Backend Entities and Repositories

## Context
Continue building the Confluence Publisher application. Create the JPA entities and Spring Data repositories for the data layer.

## Requirements

### Package Structure
All classes should be in `com.confluence.publisher` package with appropriate sub-packages:
- `entity/` - JPA entities
- `repository/` - Spring Data JPA repositories

### Entity: Page
Create `entity/Page.java`:
```java
package com.confluence.publisher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "page")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 500)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false, length = 50)
    private String spaceKey;
    
    private Long parentPageId;
    
    private Long authorId;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
```

### Entity: Attachment
Create `entity/Attachment.java`:
```java
package com.confluence.publisher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attachment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(nullable = false)
    private String contentType;
    
    @Column(nullable = false)
    private Long size;
    
    @Column(nullable = false)
    private String storagePath;
    
    @Column(columnDefinition = "TEXT")
    private String description;
}
```

### Entity: PageAttachment (Join Table)
Create `entity/PageAttachment.java`:
```java
package com.confluence.publisher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pageattachment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageAttachment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long pageId;
    
    @Column(nullable = false)
    private Long attachmentId;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer position = 0;
}
```

### Entity: Schedule
Create `entity/Schedule.java`:
```java
package com.confluence.publisher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "schedule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long pageId;
    
    @Column(nullable = false)
    private Instant scheduledAt;
    
    @Column(nullable = false)
    @Builder.Default
    private String status = "queued";  // queued, posted, failed
    
    @Column(nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;
    
    @Column(columnDefinition = "TEXT")
    private String lastError;
}
```

### Entity: PublishLog
Create `entity/PublishLog.java`:
```java
package com.confluence.publisher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "publishlog")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long pageId;
    
    @Column(nullable = false)
    private String provider;
    
    private String spaceKey;
    
    private String confluencePageId;
    
    @Column(nullable = false)
    private String status;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
```

### Repository: PageRepository
Create `repository/PageRepository.java`:
```java
package com.confluence.publisher.repository;

import com.confluence.publisher.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
}
```

### Repository: AttachmentRepository
Create `repository/AttachmentRepository.java`:
```java
package com.confluence.publisher.repository;

import com.confluence.publisher.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
```

### Repository: PageAttachmentRepository
Create `repository/PageAttachmentRepository.java`:
```java
package com.confluence.publisher.repository;

import com.confluence.publisher.entity.PageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageAttachmentRepository extends JpaRepository<PageAttachment, Long> {
    
    @Query("SELECT pa FROM PageAttachment pa WHERE pa.pageId = :pageId ORDER BY pa.position")
    List<PageAttachment> findByPageIdOrderByPosition(@Param("pageId") Long pageId);
    
    void deleteByPageId(Long pageId);
}
```

### Repository: ScheduleRepository
Create `repository/ScheduleRepository.java`:
```java
package com.confluence.publisher.repository;

import com.confluence.publisher.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    @Query("SELECT s FROM Schedule s WHERE s.status = 'queued' AND s.scheduledAt <= :now ORDER BY s.scheduledAt")
    List<Schedule> findQueuedSchedulesBefore(@Param("now") Instant now);
}
```

### Repository: PublishLogRepository
Create `repository/PublishLogRepository.java`:
```java
package com.confluence.publisher.repository;

import com.confluence.publisher.entity.PublishLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublishLogRepository extends JpaRepository<PublishLog, Long> {
}
```

## Key Design Decisions
1. **No JPA relationships**: Using simple Long IDs instead of `@ManyToOne`/`@OneToMany` for simplicity with SQLite
2. **PageAttachment join table**: Maintains order of attachments via `position` field
3. **Schedule status**: Uses string enum pattern ("queued", "posted", "failed")
4. **Timestamps**: Using `@CreationTimestamp` and `@UpdateTimestamp` from Hibernate
5. **Builder pattern**: All entities use Lombok `@Builder` for clean object construction

## Verification
- Application starts without JPA/Hibernate errors
- Tables are auto-created in SQLite database
- All repositories can perform basic CRUD operations
