# Prompt 07: Backend REST Controllers

## Role
You are an expert Java engineer.

## Task
Create REST controllers that expose the API endpoints for the Confluence Publisher application.

## Package
`com.confluence.publisher.controller`

## Controllers to Create

### 1. HealthController
Base path: `/api`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/health` | Return `{"status": "ok"}` |
| GET | `/config` | Return `{"defaultSpace": <from AppProperties>}` |

### 2. PageController
Base path: `/api/pages`

| Method | Path | Request | Response |
|--------|------|---------|----------|
| POST | `/` | PageCreateRequest | PageResponse (201 Created) |
| GET | `/{pageId}` | - | PageResponse |

**POST logic**:
- Use default space from AppProperties if spaceKey not provided
- Call PageService.createPage()
- Return created page info

### 3. AttachmentController
Base path: `/api/attachments`

| Method | Path | Request | Response |
|--------|------|---------|----------|
| POST | `/` | MultipartFile "file", optional "description" | AttachmentUploadResponse (201 Created) |

### 4. ScheduleController
Base path: `/api/schedules`

| Method | Path | Request | Response |
|--------|------|---------|----------|
| POST | `/` | ScheduleCreateRequest | ScheduleResponse (201 Created) |
| GET | `/{scheduleId}` | - | ScheduleResponse |
| GET | `/` | - | List<ScheduleResponse> |

Include private helper method `toResponse(Schedule)` to convert entity to DTO.

### 5. ConfluenceController
Base path: `/api/confluence`

| Method | Path | Request | Response |
|--------|------|---------|----------|
| POST | `/publish` | ConfluencePublishRequest | PublishResponse |

Call PublishService.publishPage() and return result.

### 6. AiController
Base path: `/api/ai`

| Method | Path | Request | Response |
|--------|------|---------|----------|
| POST | `/improve-content` | ContentImprovementRequest | ContentImprovementResponse |
| POST | `/generate-description` | AttachmentDescriptionRequest | AttachmentDescriptionResponse |

**Stub implementations**:
- `improve-content`: Return variations of input (original, truncated, uppercase)
- `generate-description`: Return sanitized/truncated description or default

## Design Guidelines
- Use `@RestController` and `@RequestMapping`
- Use `@RequiredArgsConstructor` for dependency injection
- Use `@Valid` for request body validation
- Return appropriate HTTP status codes (200, 201)
- Map entities to DTOs before returning

## API Summary

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/health` | GET | Health check |
| `/api/config` | GET | Frontend configuration |
| `/api/pages` | POST | Create page |
| `/api/pages/{id}` | GET | Get page |
| `/api/attachments` | POST | Upload file |
| `/api/schedules` | POST | Create schedule |
| `/api/schedules` | GET | List schedules |
| `/api/schedules/{id}` | GET | Get schedule |
| `/api/confluence/publish` | POST | Publish immediately |
| `/api/ai/improve-content` | POST | Content suggestions |
| `/api/ai/generate-description` | POST | Generate description |

## Verification Criteria
- All endpoints respond correctly
- Validation errors return 400
- Not found errors return 404
- CORS headers present
- File uploads work with multipart/form-data
