# Prompt 10: Frontend API Service

## Context
Continue building the Confluence Publisher application. Create the Angular service that handles all HTTP communication with the backend API.

## Requirements

### Service Location
Create the service in `src/app/services/api.service.ts`

### API Service Implementation
Create `src/app/services/api.service.ts`:
```typescript
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface Attachment {
  id: number;
  filename: string;
  description?: string;
}

export interface Schedule {
  id: number;
  pageId: number;
  status: string;
  scheduledAt: string;
  attemptCount: number;
}

export interface ContentImprovementResponse {
  suggestions: string[];
}

export interface PageResponse {
  id: number;
  title: string;
}

export interface PublishResponse {
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private http = inject(HttpClient);
  private apiBase = environment.apiBase;

  private api(path: string): string {
    return `${this.apiBase}/api${path}`;
  }

  uploadAttachment(file: File, description?: string) {
    const formData = new FormData();
    formData.append('file', file);
    if (description) {
      formData.append('description', description);
    }
    return this.http.post<Attachment>(this.api('/attachments'), formData);
  }

  improveContent(content: string) {
    return this.http.post<ContentImprovementResponse>(this.api('/ai/improve-content'), { content });
  }

  createPage(title: string, content: string, spaceKey: string | null, attachmentIds: number[], parentPageId?: number) {
    const body: any = { 
      title, 
      content, 
      parentPageId,
      attachmentIds 
    };
    // Only include spaceKey if it's provided (backend will use default if not)
    if (spaceKey) {
      body.spaceKey = spaceKey;
    }
    return this.http.post<PageResponse>(this.api('/pages'), body);
  }

  publishNow(pageId: number) {
    return this.http.post<PublishResponse>(this.api('/confluence/publish'), { pageId });
  }

  schedulePage(pageId: number) {
    return this.http.post<Schedule>(this.api('/schedules'), { pageId });
  }

  getSchedules() {
    return this.http.get<Schedule[]>(this.api('/schedules'));
  }

  getConfig() {
    return this.http.get<{ defaultSpace: string }>(this.api('/config'));
  }
}
```

## Key Design Decisions

### Service Architecture
1. **Injectable with providedIn: 'root'**: Singleton service available throughout the app
2. **inject() function**: Modern Angular dependency injection pattern
3. **Environment-based API URL**: Uses `environment.apiBase` for flexibility
4. **Helper method**: `api()` method constructs full API URLs

### TypeScript Interfaces
Define interfaces for all API responses to ensure type safety:

| Interface | Purpose |
|-----------|---------|
| `Attachment` | Uploaded file metadata |
| `Schedule` | Publication schedule details |
| `ContentImprovementResponse` | AI content suggestions |
| `PageResponse` | Created page info |
| `PublishResponse` | Publish operation result |

### API Methods

| Method | HTTP | Endpoint | Description |
|--------|------|----------|-------------|
| `uploadAttachment` | POST | `/api/attachments` | Upload file with optional description |
| `improveContent` | POST | `/api/ai/improve-content` | Get AI content suggestions |
| `createPage` | POST | `/api/pages` | Create new page with attachments |
| `publishNow` | POST | `/api/confluence/publish` | Publish page immediately |
| `schedulePage` | POST | `/api/schedules` | Schedule page for publication |
| `getSchedules` | GET | `/api/schedules` | List all schedules |
| `getConfig` | GET | `/api/config` | Get app configuration |

### FormData for File Uploads
```typescript
uploadAttachment(file: File, description?: string) {
  const formData = new FormData();
  formData.append('file', file);
  if (description) {
    formData.append('description', description);
  }
  return this.http.post<Attachment>(this.api('/attachments'), formData);
}
```

### Optional Parameters
```typescript
createPage(title: string, content: string, spaceKey: string | null, attachmentIds: number[], parentPageId?: number) {
  const body: any = { title, content, parentPageId, attachmentIds };
  if (spaceKey) {
    body.spaceKey = spaceKey;
  }
  return this.http.post<PageResponse>(this.api('/pages'), body);
}
```

## Usage in Components
```typescript
// In a component
private apiService = inject(ApiService);

async uploadFile() {
  const result = await firstValueFrom(this.apiService.uploadAttachment(file, 'My description'));
  console.log('Uploaded:', result.id);
}

async createAndPublish() {
  const page = await firstValueFrom(this.apiService.createPage('Title', 'Content', 'DEV', [1, 2]));
  const result = await firstValueFrom(this.apiService.publishNow(page.id));
  console.log('Published:', result.status);
}
```

## Verification
- Service compiles without errors
- All methods return properly typed Observables
- File uploads work with FormData
- API base URL is configurable via environment
