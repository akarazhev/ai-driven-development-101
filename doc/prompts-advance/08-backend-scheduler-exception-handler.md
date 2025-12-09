# Prompt 08: Backend Scheduler and Exception Handler

## Context
Continue building the Confluence Publisher application. Create the background scheduler for processing queued publications and the global exception handler for consistent error responses.

## Requirements

### Package Structure
- Scheduler: `com.confluence.publisher.scheduler`
- Exception handler: `com.confluence.publisher.exception`

### Scheduler: PageScheduler
Create `scheduler/PageScheduler.java`:
```java
package com.confluence.publisher.scheduler;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.entity.PublishLog;
import com.confluence.publisher.entity.Schedule;
import com.confluence.publisher.service.PublishService;
import com.confluence.publisher.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PageScheduler {
    
    private final ScheduleService scheduleService;
    private final PublishService publishService;
    private final AppProperties appProperties;
    
    @Scheduled(fixedDelayString = "#{@appProperties.schedulerIntervalSeconds * 1000}")
    public void processScheduledPosts() {
        Instant now = Instant.now();
        List<Schedule> queuedSchedules = scheduleService.findQueuedSchedules(now);
        
        for (Schedule schedule : queuedSchedules) {
            try {
                PublishLog publishLog = publishService.publishPage(schedule.getPageId());
                scheduleService.updateScheduleStatus(schedule, "posted", null);
                log.debug("Successfully published page {} for schedule {}", schedule.getPageId(), schedule.getId());
            } catch (Exception e) {
                log.error("Failed to publish page {} for schedule {}", schedule.getPageId(), schedule.getId(), e);
                scheduleService.updateScheduleStatus(schedule, "failed", e.getMessage());
            }
        }
    }
}
```

### Exception Handler: GlobalExceptionHandler
Create `exception/GlobalExceptionHandler.java`:
```java
package com.confluence.publisher.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception", e);
        Map<String, String> error = new HashMap<>();
        error.put("detail", e.getMessage());
        
        if (e.getMessage() != null && e.getMessage().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        log.error("Unexpected exception", e);
        Map<String, String> error = new HashMap<>();
        error.put("detail", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

## Key Design Decisions

### Scheduler
1. **SpEL expression**: Uses `#{@appProperties.schedulerIntervalSeconds * 1000}` to read interval from config
2. **Fixed delay**: Uses `fixedDelayString` so next execution starts after previous completes
3. **Error isolation**: Each schedule is processed independently; one failure doesn't stop others
4. **Status tracking**: Updates schedule status to "posted" or "failed" with error message
5. **Attempt counting**: ScheduleService increments attempt count on each try

### Exception Handler
1. **@RestControllerAdvice**: Applies to all REST controllers
2. **Not found detection**: Checks message for "not found" to return 404
3. **Validation errors**: Returns field-level errors for validation failures
4. **Generic fallback**: Catches all other exceptions with generic 500 response
5. **Logging**: All exceptions are logged for debugging

## Scheduler Flow
```
┌─────────────────────────────────────────────────────────┐
│                    PageScheduler                         │
│                                                          │
│  Every N seconds (configurable):                         │
│  1. Find all schedules with status="queued"              │
│     and scheduledAt <= now                               │
│  2. For each schedule:                                   │
│     a. Call PublishService.publishPage(pageId)           │
│     b. On success: status="posted"                       │
│     c. On failure: status="failed", store error          │
│  3. Increment attemptCount                               │
└─────────────────────────────────────────────────────────┘
```

## Error Response Formats

### Validation Error (400)
```json
{
  "errors": {
    "title": "Title is required",
    "content": "Content is required"
  }
}
```

### Not Found Error (404)
```json
{
  "detail": "Page not found: 123"
}
```

### Server Error (500)
```json
{
  "detail": "Internal server error"
}
```

## Verification
- Scheduler runs at configured interval
- Queued schedules are processed automatically
- Failed schedules have error messages stored
- Validation errors return proper field-level messages
- Not found errors return 404 status
- Generic errors don't expose internal details
