# Prompt 11: Frontend Compose Component

## Role
You are an expert front-end engineer.

## Task
Create the main Compose page component for creating pages, uploading attachments, and publishing to Confluence.

## Location
`src/app/pages/compose/compose.component.ts`

## Component Configuration
- Standalone component
- Imports: CommonModule, FormsModule
- ChangeDetection: OnPush
- Inline template

## State (using Angular Signals)

| Signal | Type | Initial |
|--------|------|---------|
| title | string | '' |
| content | string | '' |
| spaceKey | string | '' |
| files | File[] | [] |
| descriptions | string[] | [] |
| attachments | Attachment[] | [] |
| busy | boolean | false |
| suggestions | string[] | [] |
| pageId | number \| null | null |
| scheduleId | number \| null | null |

**Computed**:
- `canUpload` = files.length > 0

## Constructor
- Load default space from `apiService.getConfig()` and set spaceKey

## Template Sections

### Page Creation Section
- Title input field
- Space key input field (optional)
- Content textarea (8 rows)
- Action buttons:
  - "Improve content" (disabled if no content or busy)
  - "Create page" (disabled if no title/content or busy)
  - "Publish now" (disabled if no pageId or busy)
  - "Schedule" (disabled if no pageId or busy)
- Suggestions list (if any) - clickable to replace content

### Attachments Section
- File input (multiple)
- For each selected file: filename + description input
- "Upload" button
- List of uploaded attachments

## Methods

| Method | Description |
|--------|-------------|
| onFiles(event) | Set files and initialize descriptions array |
| updateDescription(index, value) | Update description at index |
| uploadAll() | Upload all files, add to attachments, clear files |
| improvContent() | Call API, set suggestions |
| createPage() | Call API, set pageId, show alert |
| publishNow() | Call API, show status alert |
| schedule() | Call API, set scheduleId, show alert |

## Async Pattern
Use `firstValueFrom()` to convert Observables to Promises for async/await.

## Error Handling
- Set busy=false in finally block
- Show alert on error
- Log errors to console

## Styling (TailwindCSS)
- Sections with spacing (space-y-8)
- Inputs with border, rounded, padding, focus ring
- Buttons with colors: blue (improve), green (create), purple (publish), amber (schedule)
- Disabled state with opacity-50

## Verification Criteria
- Form inputs work correctly
- File uploads complete successfully
- Page creation returns page ID
- Publish/Schedule enabled after page creation
- Suggestions are clickable
- Busy state disables buttons
