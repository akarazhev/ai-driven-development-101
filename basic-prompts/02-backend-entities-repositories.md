# Prompt 02: Backend Entities and Repositories

## Role
You are an expert Java engineer.

## Task
Create the JPA entities and Spring Data repositories for the Confluence Publisher data layer.

## Package Structure
- `com.confluence.publisher.entity` - JPA entities
- `com.confluence.publisher.repository` - Spring Data repositories

## Entities to Create

### 1. Page Entity
| Field | Type | Constraints |
|-------|------|-------------|
| id | Long | Primary key, auto-generated |
| title | String | Required, max 500 chars |
| content | String | Required, TEXT type |
| spaceKey | String | Required, max 50 chars |
| parentPageId | Long | Optional, reference to parent page |
| authorId | Long | Optional |
| createdAt | Instant | Auto-set on creation |
| updatedAt | Instant | Auto-updated on modification |

### 2. Attachment Entity
| Field | Type | Constraints |
|-------|------|-------------|
| id | Long | Primary key, auto-generated |
| filename | String | Required, original filename |
| contentType | String | Required, MIME type |
| size | Long | Required, file size in bytes |
| storagePath | String | Required, path on disk |
| description | String | Optional, TEXT type |

### 3. PageAttachment Entity (Join Table)
| Field | Type | Constraints |
|-------|------|-------------|
| id | Long | Primary key, auto-generated |
| pageId | Long | Required |
| attachmentId | Long | Required |
| position | Integer | Required, default 0, for ordering |

### 4. Schedule Entity
| Field | Type | Constraints |
|-------|------|-------------|
| id | Long | Primary key, auto-generated |
| pageId | Long | Required |
| scheduledAt | Instant | Required, when to publish |
| status | String | Required, default "queued" (queued/posted/failed) |
| attemptCount | Integer | Required, default 0 |
| lastError | String | Optional, TEXT type, error message |

### 5. PublishLog Entity
| Field | Type | Constraints |
|-------|------|-------------|
| id | Long | Primary key, auto-generated |
| pageId | Long | Required |
| provider | String | Required, provider name used |
| spaceKey | String | Optional |
| confluencePageId | String | Optional, ID from Confluence |
| status | String | Required |
| message | String | Optional, TEXT type |
| createdAt | Instant | Auto-set on creation |

## Repositories to Create

| Repository | Custom Methods |
|------------|----------------|
| PageRepository | Standard CRUD only |
| AttachmentRepository | Standard CRUD only |
| PageAttachmentRepository | `findByPageIdOrderByPosition(Long pageId)`, `deleteByPageId(Long pageId)` |
| ScheduleRepository | `findQueuedSchedulesBefore(Instant now)` - finds schedules with status="queued" and scheduledAt <= now |
| PublishLogRepository | Standard CRUD only |

## Design Guidelines
- Use Lombok annotations: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Use `@CreationTimestamp` and `@UpdateTimestamp` for automatic timestamps
- Use simple Long IDs instead of JPA relationships (simpler for SQLite)
- Use `@Builder.Default` for fields with default values

## Verification Criteria
- Application starts without JPA/Hibernate errors
- Tables are auto-created in SQLite database
- All repositories can perform basic CRUD operations
