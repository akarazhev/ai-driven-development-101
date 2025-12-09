# Prompt 10: Frontend API Service

## Role
You are an expert front-end engineer.

## Task
Create the Angular service that handles all HTTP communication with the backend API.

## Location
`src/app/services/api.service.ts`

## TypeScript Interfaces to Define

| Interface | Fields |
|-----------|--------|
| Attachment | id: number, filename: string, description?: string |
| Schedule | id: number, pageId: number, status: string, scheduledAt: string, attemptCount: number |
| ContentImprovementResponse | suggestions: string[] |
| PageResponse | id: number, title: string |
| PublishResponse | status: string |

## ApiService Class

**Setup**:
- Injectable with `providedIn: 'root'`
- Inject HttpClient using `inject()` function
- Read apiBase from environment

**Helper Method**:
- `api(path: string)` - returns full URL: `${apiBase}/api${path}`

## API Methods

| Method | HTTP | Endpoint | Parameters | Returns |
|--------|------|----------|------------|---------|
| uploadAttachment | POST | /attachments | File, description? | Observable<Attachment> |
| improveContent | POST | /ai/improve-content | content: string | Observable<ContentImprovementResponse> |
| createPage | POST | /pages | title, content, spaceKey, attachmentIds, parentPageId? | Observable<PageResponse> |
| publishNow | POST | /confluence/publish | pageId: number | Observable<PublishResponse> |
| schedulePage | POST | /schedules | pageId: number | Observable<Schedule> |
| getSchedules | GET | /schedules | - | Observable<Schedule[]> |
| getConfig | GET | /config | - | Observable<{defaultSpace: string}> |

## Implementation Notes

**File Upload**:
- Create FormData
- Append 'file' and optional 'description'
- POST without explicit Content-Type (browser sets multipart boundary)

**Optional Parameters**:
- Only include spaceKey in body if provided
- Backend uses default if not sent

## Usage Example
```typescript
// In component
private apiService = inject(ApiService);

async upload() {
  const result = await firstValueFrom(this.apiService.uploadAttachment(file));
}
```

## Verification Criteria
- Service compiles without errors
- All methods return typed Observables
- File uploads work with FormData
- API base URL configurable via environment
