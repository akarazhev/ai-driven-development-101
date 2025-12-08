# Confluence Publisher API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
Currently, the API does not require authentication. This is a development/stub implementation.

---

## Endpoints

### Health Check

#### GET `/api/health`
Check if the API is running.

**Response:**
```json
{
  "status": "ok"
}
```

**Status Codes:**
- `200 OK` - Service is healthy

---

### Pages

#### POST `/api/pages`
Create a new Confluence page.

**Request Body:**
```json
{
  "title": "string (required)",
  "content": "string (required)",
  "spaceKey": "string (required)",
  "parentPageId": "number (optional)",
  "attachmentIds": [1, 2, 3] // optional array of attachment IDs
}
```

**Example Request:**
```json
{
  "title": "My New Page",
  "content": "This is the page content",
  "spaceKey": "DEV",
  "parentPageId": null,
  "attachmentIds": [1, 2]
}
```

**Response:**
```json
{
  "id": 1,
  "title": "My New Page",
  "content": "This is the page content",
  "spaceKey": "DEV",
  "parentPageId": null,
  "attachments": []
}
```

**Status Codes:**
- `201 Created` - Page created successfully
- `400 Bad Request` - Validation error (missing required fields, empty values)
- `404 Not Found` - Attachment ID not found (if provided)
- `500 Internal Server Error` - Server error

**Validation Rules:**
- `title`: Required, cannot be empty or null
- `content`: Required, cannot be empty or null
- `spaceKey`: Required, cannot be empty or null
- `parentPageId`: Optional
- `attachmentIds`: Optional array, all IDs must exist

---

#### GET `/api/pages/{pageId}`
Retrieve a page by ID.

**Path Parameters:**
- `pageId` (Long, required) - The ID of the page to retrieve

**Response:**
```json
{
  "id": 1,
  "title": "My New Page",
  "content": "This is the page content",
  "spaceKey": "DEV",
  "parentPageId": null,
  "attachments": [
    {
      "id": 1,
      "filename": "document.pdf",
      "description": "Project documentation"
    }
  ]
}
```

**Status Codes:**
- `200 OK` - Page retrieved successfully
- `400 Bad Request` - Invalid page ID
- `404 Not Found` - Page not found
- `500 Internal Server Error` - Server error

---

### Attachments

#### POST `/api/attachments`
Upload a file attachment.

**Request:**
- Content-Type: `multipart/form-data`
- Parameters:
  - `file` (File, required) - The file to upload (max 10MB)
  - `description` (String, optional) - Description of the attachment

**Example Request (cURL):**
```bash
curl -X POST http://localhost:8080/api/attachments \
  -F "file=@document.pdf" \
  -F "description=Project documentation"
```

**Response:**
```json
{
  "id": 1,
  "filename": "document.pdf",
  "description": "Project documentation"
}
```

**Status Codes:**
- `201 Created` - Attachment uploaded successfully
- `400 Bad Request` - Validation error (empty file, missing filename, file too large)
- `413 Payload Too Large` - File exceeds 10MB limit
- `500 Internal Server Error` - Server error (e.g., file system error)

**Validation Rules:**
- `file`: Required, cannot be empty
- `filename`: Required (from uploaded file)
- `description`: Optional
- Maximum file size: 10MB

---

### Schedules

#### POST `/api/schedules`
Create a new publication schedule.

**Request Body:**
```json
{
  "pageId": 1,
  "scheduledAt": "2024-12-31T23:59:59Z" // ISO 8601 format, optional (defaults to now)
}
```

**Example Request:**
```json
{
  "pageId": 1,
  "scheduledAt": "2024-12-31T23:59:59Z"
}
```

**Response:**
```json
{
  "id": 1,
  "pageId": 1,
  "status": "queued",
  "scheduledAt": "2024-12-31T23:59:59Z",
  "attemptCount": 0,
  "lastError": null
}
```

**Status Codes:**
- `201 Created` - Schedule created successfully
- `400 Bad Request` - Validation error (missing page ID, invalid page ID)
- `404 Not Found` - Page not found
- `500 Internal Server Error` - Server error

**Validation Rules:**
- `pageId`: Required, must exist
- `scheduledAt`: Optional, ISO 8601 format. If not provided, defaults to current time

**Schedule Statuses:**
- `queued` - Scheduled but not yet published
- `published` - Successfully published
- `failed` - Publication failed

---

#### GET `/api/schedules`
List all schedules (limited to 100 most recent).

**Response:**
```json
[
  {
    "id": 1,
    "pageId": 1,
    "status": "queued",
    "scheduledAt": "2024-12-31T23:59:59Z",
    "attemptCount": 0,
    "lastError": null
  },
  {
    "id": 2,
    "pageId": 2,
    "status": "published",
    "scheduledAt": "2024-12-30T12:00:00Z",
    "attemptCount": 1,
    "lastError": null
  }
]
```

**Status Codes:**
- `200 OK` - Schedules retrieved successfully
- `500 Internal Server Error` - Server error

---

#### GET `/api/schedules/{scheduleId}`
Retrieve a schedule by ID.

**Path Parameters:**
- `scheduleId` (Long, required) - The ID of the schedule to retrieve

**Response:**
```json
{
  "id": 1,
  "pageId": 1,
  "status": "queued",
  "scheduledAt": "2024-12-31T23:59:59Z",
  "attemptCount": 0,
  "lastError": null
}
```

**Status Codes:**
- `200 OK` - Schedule retrieved successfully
- `400 Bad Request` - Invalid schedule ID
- `404 Not Found` - Schedule not found
- `500 Internal Server Error` - Server error

---

### Confluence Publishing

#### POST `/api/confluence/publish`
Publish a page to Confluence immediately.

**Request Body:**
```json
{
  "pageId": 1
}
```

**Example Request:**
```json
{
  "pageId": 1
}
```

**Response:**
```json
{
  "logId": 1,
  "status": "published",
  "confluencePageId": "12345"
}
```

**Status Codes:**
- `200 OK` - Page published successfully
- `400 Bad Request` - Validation error (missing page ID)
- `404 Not Found` - Page not found
- `500 Internal Server Error` - Publishing failed

**Validation Rules:**
- `pageId`: Required, must exist

**Note:** This endpoint uses a stub provider in development mode. The `confluencePageId` will be a generated string, not a real Confluence page ID.

---

### AI Features

#### POST `/api/ai/improve-content`
Generate content improvement suggestions.

**Request Body:**
```json
{
  "content": "string (required)"
}
```

**Example Request:**
```json
{
  "content": "This is a short content that needs improvement."
}
```

**Response:**
```json
{
  "suggestions": [
    "This is a short content that needs improvement.",
    "This is a short content that needs improvement. #update",
    "THIS IS A SHORT CONTENT THAT NEEDS IMPROVEMENT."
  ]
}
```

**Status Codes:**
- `200 OK` - Suggestions generated successfully
- `400 Bad Request` - Validation error (missing content)
- `500 Internal Server Error` - Server error

**Validation Rules:**
- `content`: Required, cannot be null

**Note:** This is a stub implementation that generates simple variations of the input content. In production, this would use an AI service.

---

#### POST `/api/ai/generate-description`
Generate a description for an attachment.

**Request Body:**
```json
{
  "description": "string (optional)"
}
```

**Example Request:**
```json
{
  "description": "Project documentation file"
}
```

**Response:**
```json
{
  "description": "Project documentation file"
}
```

**Status Codes:**
- `200 OK` - Description generated successfully
- `500 Internal Server Error` - Server error

**Note:** 
- If `description` is provided, it will be truncated to 120 characters if longer
- If `description` is not provided or empty, defaults to "Document attachment"
- This is a stub implementation. In production, this would use an AI service to generate descriptions.

---

## Error Responses

All error responses follow a consistent format:

```json
{
  "timestamp": "2024-12-19T14:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Page title cannot be empty"
}
```

For validation errors with multiple fields:

```json
{
  "timestamp": "2024-12-19T14:30:00Z",
  "status": 400,
  "error": "Validation Failed",
  "details": {
    "title": "Page title cannot be empty",
    "content": "Page content cannot be empty"
  }
}
```

### Error Status Codes

- `400 Bad Request` - Validation error or invalid request
- `404 Not Found` - Resource not found
- `413 Payload Too Large` - File upload exceeds size limit
- `500 Internal Server Error` - Unexpected server error

---

## Data Models

### PageResponse
```typescript
{
  id: number;
  title: string;
  content: string;
  spaceKey: string;
  parentPageId: number | null;
  attachments: AttachmentInfo[];
}

interface AttachmentInfo {
  id: number;
  filename: string;
  description: string | null;
}
```

### ScheduleResponse
```typescript
{
  id: number;
  pageId: number;
  status: "queued" | "published" | "failed";
  scheduledAt: string; // ISO 8601
  attemptCount: number;
  lastError: string | null;
}
```

### PublishResponse
```typescript
{
  logId: number;
  status: string;
  confluencePageId: string;
}
```

### AttachmentUploadResponse
```typescript
{
  id: number;
  filename: string;
  description: string | null;
}
```

### ContentImprovementResponse
```typescript
{
  suggestions: string[];
}
```

### AttachmentDescriptionResponse
```typescript
{
  description: string;
}
```

---

## Example Workflows

### Complete Page Creation and Publishing Workflow

1. **Upload Attachments:**
```bash
curl -X POST http://localhost:8080/api/attachments \
  -F "file=@document.pdf" \
  -F "description=Project documentation"
```
Response: `{ "id": 1, "filename": "document.pdf", "description": "Project documentation" }`

2. **Create Page:**
```bash
curl -X POST http://localhost:8080/api/pages \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My New Page",
    "content": "This is the page content",
    "spaceKey": "DEV",
    "attachmentIds": [1]
  }'
```
Response: `{ "id": 1, "title": "My New Page", ... }`

3. **Publish Page:**
```bash
curl -X POST http://localhost:8080/api/confluence/publish \
  -H "Content-Type: application/json" \
  -d '{"pageId": 1}'
```
Response: `{ "logId": 1, "status": "published", "confluencePageId": "12345" }`

### Scheduled Publishing Workflow

1. **Create Page** (same as above)

2. **Schedule Publication:**
```bash
curl -X POST http://localhost:8080/api/schedules \
  -H "Content-Type: application/json" \
  -d '{
    "pageId": 1,
    "scheduledAt": "2024-12-31T23:59:59Z"
  }'
```
Response: `{ "id": 1, "pageId": 1, "status": "queued", ... }`

3. **Check Schedule Status:**
```bash
curl http://localhost:8080/api/schedules/1
```

---

## Rate Limiting

Currently, there are no rate limits implemented. In production, rate limiting should be added.

## CORS

The API is configured to allow CORS from `http://localhost:4200` (Angular dev server). For production, update CORS configuration in `CorsConfig.java`.

---

## Testing

### Using cURL

All endpoints can be tested using cURL. See example workflows above.

### Using Postman

A Postman collection is available at `postman/ConfluencePublisher.postman_collection.json`.

### Using Playwright

E2E tests are available in `frontend/e2e/tests/`.

---

## Notes

- This API uses a **stub Confluence provider** in development mode. Real Confluence integration requires configuration of Confluence credentials.
- File uploads are stored in `storage/attachments/` directory (configurable).
- Database is SQLite, stored in `data/app.db` (configurable).
- All timestamps are in ISO 8601 format (UTC).

