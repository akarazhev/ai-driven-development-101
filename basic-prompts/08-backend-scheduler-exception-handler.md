# Prompt 08: Backend Scheduler and Exception Handler

## Role
You are an expert Java engineer.

## Task
Create the background scheduler for processing queued publications and the global exception handler for consistent error responses.

## Components to Create

### 1. PageScheduler
Package: `com.confluence.publisher.scheduler`

**Dependencies**: ScheduleService, PublishService, AppProperties

**Scheduled Method**: `processScheduledPosts()`
- Annotate with `@Scheduled(fixedDelayString = "#{@appProperties.schedulerIntervalSeconds * 1000}")`
- Find all queued schedules where scheduledAt <= now
- For each schedule:
  - Try to publish the page via PublishService
  - On success: update status to "posted"
  - On failure: update status to "failed" with error message
- Log results

### 2. GlobalExceptionHandler
Package: `com.confluence.publisher.exception`

Use `@RestControllerAdvice` to handle exceptions globally.

**Exception Handlers**:

| Exception | Status | Response |
|-----------|--------|----------|
| RuntimeException | 404 if message contains "not found", else 500 | `{"detail": <message>}` |
| MethodArgumentNotValidException | 400 | `{"errors": {<field>: <message>, ...}}` |
| Exception (generic) | 500 | `{"detail": "Internal server error"}` |

**Behavior**:
- Log all exceptions
- Extract field errors from validation exceptions
- Don't expose internal details for generic exceptions

## Scheduler Flow

```
Every N seconds:
1. Query: SELECT * FROM schedule WHERE status='queued' AND scheduledAt <= NOW
2. For each schedule:
   - Call publishService.publishPage(pageId)
   - Update schedule status and attemptCount
3. Log success/failure
```

## Error Response Formats

**Validation Error (400)**:
```json
{"errors": {"title": "Title is required", "content": "Content is required"}}
```

**Not Found (404)**:
```json
{"detail": "Page not found: 123"}
```

**Server Error (500)**:
```json
{"detail": "Internal server error"}
```

## Verification Criteria
- Scheduler runs at configured interval
- Queued schedules processed automatically
- Failed schedules store error messages
- Validation errors return field-level messages
- Not found errors return 404
- Generic errors don't expose internals
