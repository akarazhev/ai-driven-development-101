# Prompt 05: Backend Services

## Role
You are an expert Java engineer.

## Task
Create the service layer with business logic for the Confluence Publisher application.

## Package
`com.confluence.publisher.service`

## Services to Create

### 1. PageService

**Dependencies**: PageRepository, PageAttachmentRepository, AttachmentRepository

**Methods**:
- `createPage(title, content, spaceKey, parentPageId, attachmentIds)` → Page
  - Save the page entity
  - Create PageAttachment records for each attachment ID with position index
  - Return the saved page

- `getPage(pageId)` → PageResponse
  - Find page by ID or throw "Page not found" exception
  - Load associated attachments via PageAttachmentRepository
  - Build and return PageResponse with attachment info

### 2. AttachmentService

**Dependencies**: AttachmentRepository, AppProperties

**Methods**:
- `uploadAttachment(MultipartFile file, description)` → Attachment
  - Create attachment directory if needed
  - Generate UUID-based filename preserving original extension
  - Write file bytes to disk
  - Create Attachment entity with original filename, content type, size, storage path
  - Save and return the attachment

### 3. ScheduleService

**Dependencies**: ScheduleRepository

**Methods**:
- `createSchedule(pageId, scheduledAt)` → Schedule
  - Use current time if scheduledAt is null
  - Create schedule with status "queued"
  
- `getSchedule(scheduleId)` → Schedule
  - Find by ID or throw "Schedule not found"

- `listSchedules(limit)` → List<Schedule>
  - Return schedules sorted by ID descending with pagination

- `findQueuedSchedules(now)` → List<Schedule>
  - Find schedules with status "queued" and scheduledAt <= now

- `updateScheduleStatus(schedule, status, error)` → void
  - Update status, increment attemptCount, set lastError

### 4. PublishService

**Dependencies**: PageRepository, PageAttachmentRepository, AttachmentRepository, PublishLogRepository, ProviderFactory

**Methods**:
- `publishPage(pageId)` → PublishLog
  - Find page by ID
  - Get attachment file paths for the page
  - Get provider from ProviderFactory
  - Call provider.publishPage() with page data
  - Create and save PublishLog with result
  - Return the publish log

## Design Guidelines
- Use `@Service` and `@RequiredArgsConstructor`
- Use `@Transactional` for write operations
- Use `@Transactional(readOnly = true)` for read operations
- Throw `RuntimeException` with descriptive messages for not-found cases
- Use Lombok `@Slf4j` for logging

## Verification Criteria
- All services compile and can be injected
- Transactions rollback on errors
- File uploads saved to configured directory
- Page-attachment relationships maintained correctly
