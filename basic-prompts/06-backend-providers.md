# Prompt 06: Backend Confluence Providers

## Role
You are an expert Java engineer.

## Task
Create the provider pattern for Confluence API integration with a stub for testing and a real server provider.

## Package Structure
- `com.confluence.publisher.provider` - Provider classes
- `com.confluence.publisher.provider.dto` - Confluence API DTOs

## Components to Create

### 1. BaseProvider Interface

Define an interface with:
- `publishPage(spaceKey, title, content, parentPageId, attachmentPaths)` → ProviderResult
- `getStatus(confluencePageId)` → String
- Inner record `ProviderResult(String confluencePageId, String message)`

### 2. ConfluenceStubProvider

A stub implementation for testing:
- Generate fake page ID like "CONF-" + random UUID substring
- Log the operation details
- Return success result without making real API calls

### 3. ProviderFactory

Factory to select provider based on configuration:
- Inject AppProperties, ConfluenceStubProvider, ConfluenceServerProvider
- `getProvider()` method returns provider based on `app.provider` config:
  - "confluence-server" or "server" → ConfluenceServerProvider
  - "confluence-stub" or "stub" → ConfluenceStubProvider
  - Unknown → fallback to stub with warning
- `getProviderName()` returns configured provider name

### 4. Confluence API DTOs

**ConfluencePageRequest** (for creating/updating pages):
- type, title, space (nested: key), body (nested: storage with value and representation)
- ancestors (list of id), version (nested: number)
- Use `@JsonInclude(NON_NULL)` to omit null fields

**ConfluencePageResponse** (API response):
- id, type, status, title, space, version, _links (webui, self)
- Use `@JsonIgnoreProperties(ignoreUnknown = true)`

**ConfluenceSearchResponse**:
- results (List<ConfluencePageResponse>), start, limit, size

### 5. ConfluenceServerProvider

Real Confluence integration using Spring's RestClient:

**RestClient Setup**:
- Lazy initialization with double-checked locking
- Base URL from AppProperties (remove trailing slash)
- Authentication: Bearer token if API token > 30 chars, otherwise Basic auth
- Default headers: Authorization, Content-Type, Accept

**publishPage Method**:
1. Search for existing page by title in space
2. If exists → update page (increment version number)
3. If not exists → create new page
4. Upload attachments if any
5. Return ProviderResult with page ID and web URL

**Helper Methods**:
- `findPageByTitle(spaceKey, title)` - GET /rest/api/content with query params
- `createPage(...)` - POST /rest/api/content
- `updatePage(...)` - PUT /rest/api/content/{id} with incremented version
- `uploadAttachments(pageId, paths)` - POST multipart to /rest/api/content/{id}/child/attachment
- `buildWebUrl(page)` - construct web URL from _links.webui

**Attachment Upload**:
- Use MultipartBodyBuilder with FileSystemResource
- Include header `X-Atlassian-Token: nocheck`

## Confluence REST API Endpoints

| Operation | Method | Endpoint |
|-----------|--------|----------|
| Search | GET | `/rest/api/content?spaceKey=X&title=Y&expand=version` |
| Create | POST | `/rest/api/content` |
| Update | PUT | `/rest/api/content/{id}` |
| Get | GET | `/rest/api/content/{id}` |
| Attach | POST | `/rest/api/content/{id}/child/attachment` |

## Verification Criteria
- Stub provider logs without API calls
- Server provider authenticates correctly
- Pages created/updated with proper versioning
- Attachments uploaded successfully
