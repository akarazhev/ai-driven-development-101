# Prompt 03: Backend DTOs (Data Transfer Objects)

## Role
You are an expert Java engineer.

## Task
Create the DTOs for API request/response handling in the Confluence Publisher application.

## Package
`com.confluence.publisher.dto`

## Request DTOs

### PageCreateRequest
| Field | Type | Validation |
|-------|------|------------|
| title | String | @NotBlank |
| content | String | @NotBlank |
| spaceKey | String | Optional (uses default if empty) |
| parentPageId | Long | Optional |
| attachmentIds | List<Long> | @NotNull, default empty list |

### ScheduleCreateRequest
| Field | Type | Validation |
|-------|------|------------|
| pageId | Long | @NotNull |
| scheduledAt | Instant | Optional (defaults to now) |

### ConfluencePublishRequest
| Field | Type | Validation |
|-------|------|------------|
| pageId | Long | @NotNull |

### ContentImprovementRequest
| Field | Type | Validation |
|-------|------|------------|
| content | String | @NotBlank |

### AttachmentDescriptionRequest
| Field | Type | Validation |
|-------|------|------------|
| description | String | Optional |

## Response DTOs

### PageResponse
| Field | Type |
|-------|------|
| id | Long |
| title | String |
| content | String |
| spaceKey | String |
| parentPageId | Long |
| attachments | List<AttachmentInfo> |

Include nested static class `AttachmentInfo` with: id, filename, description

### AttachmentUploadResponse
| Field | Type |
|-------|------|
| id | Long |
| filename | String |
| description | String |

### ScheduleResponse
| Field | Type |
|-------|------|
| id | Long |
| pageId | Long |
| status | String |
| scheduledAt | Instant |
| attemptCount | Integer |
| lastError | String |

### PublishResponse
| Field | Type |
|-------|------|
| logId | Long |
| status | String |
| confluencePageId | String |

### ContentImprovementResponse
| Field | Type |
|-------|------|
| suggestions | List<String> |

### AttachmentDescriptionResponse
| Field | Type |
|-------|------|
| description | String |

## Design Guidelines
- Request DTOs: Use `@Data` from Lombok, Jakarta Validation annotations
- Response DTOs: Use `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Use nested static classes for complex response structures

## Verification Criteria
- All DTOs compile without errors
- Validation annotations trigger on invalid input
- Jackson can serialize/deserialize all DTOs
