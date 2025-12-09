# Prompt 11: Frontend Compose Component

## Context
Continue building the Confluence Publisher application. Create the main Compose page component that allows users to create pages, upload attachments, and publish to Confluence.

## Requirements

### Component Location
Create the component in `src/app/pages/compose/compose.component.ts`

### Compose Component Implementation
Create `src/app/pages/compose/compose.component.ts`:
```typescript
import { Component, signal, computed, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { ApiService, Attachment } from '../../services/api.service';

@Component({
  selector: 'app-compose',
  imports: [CommonModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="space-y-8">
      <section class="space-y-3">
        <h2 class="text-base font-semibold">Create Confluence Page</h2>
        <input
          type="text"
          class="w-full border rounded p-3 focus:outline-none focus:ring"
          placeholder="Page title..."
          [value]="title()"
          (input)="title.set($any($event.target).value)"
        />
        <input
          type="text"
          class="w-full border rounded p-2 text-sm focus:outline-none focus:ring"
          placeholder="Space key (e.g., DEV, DOCS)"
          [value]="spaceKey()"
          (input)="spaceKey.set($any($event.target).value)"
        />
        <textarea
          class="w-full border rounded p-3 focus:outline-none focus:ring"
          rows="8"
          placeholder="Page content..."
          [value]="content()"
          (input)="content.set($any($event.target).value)"
        ></textarea>
        <div class="flex gap-2">
          <button 
            (click)="improvContent()" 
            [disabled]="busy() || !content()" 
            class="px-3 py-2 bg-blue-600 text-white rounded disabled:opacity-50">
            Improve content
          </button>
          <button 
            (click)="createPage()" 
            [disabled]="busy() || !title() || !content()" 
            class="px-3 py-2 bg-green-600 text-white rounded disabled:opacity-50">
            Create page
          </button>
          <button 
            (click)="publishNow()" 
            [disabled]="busy() || !pageId()" 
            class="px-3 py-2 bg-purple-600 text-white rounded disabled:opacity-50">
            Publish now
          </button>
          <button 
            (click)="schedule()" 
            [disabled]="busy() || !pageId()" 
            class="px-3 py-2 bg-amber-600 text-white rounded disabled:opacity-50">
            Schedule
          </button>
        </div>
        @if (suggestions().length > 0) {
          <div class="bg-white border rounded p-3">
            <h3 class="font-medium mb-2">Content Suggestions</h3>
            <ul class="list-disc pl-5 space-y-1">
              @for (suggestion of suggestions(); track $index) {
                <li>
                  <button 
                    class="text-blue-600 hover:underline" 
                    (click)="content.set(suggestion)">
                    {{ suggestion.substring(0, 100) }}...
                  </button>
                </li>
              }
            </ul>
          </div>
        }
      </section>

      <section class="space-y-3">
        <h2 class="text-base font-semibold">Attachments</h2>
        <input 
          type="file" 
          multiple 
          (change)="onFiles($event)"
          #fileInput
        />
        @if (files().length > 0) {
          <div class="space-y-2">
            @for (file of files(); track $index; let idx = $index) {
              <div class="flex items-center gap-2 text-sm">
                <div class="text-gray-600">{{ file.name }}</div>
                <input 
                  #descInput
                  class="border rounded p-1 flex-1" 
                  placeholder="Description (optional)"
                  [value]="descriptions()[idx]"
                  (input)="updateDescription(idx, $any($event.target).value)"
                />
              </div>
            }
            <button 
              (click)="uploadAll()" 
              [disabled]="busy() || !canUpload()" 
              class="px-3 py-2 bg-gray-800 text-white rounded disabled:opacity-50">
              Upload
            </button>
          </div>
        }
        @if (attachments().length > 0) {
          <div class="bg-white border rounded p-3">
            <h3 class="font-medium mb-2">Attached Files</h3>
            <ul class="list-disc pl-5 text-sm space-y-1">
              @for (att of attachments(); track att.id) {
                <li>
                  {{ att.filename }} 
                  @if (att.description) {
                    ({{ att.description }})
                  }
                </li>
              }
            </ul>
          </div>
        }
      </section>
    </div>
  `
})
export class ComposeComponent {
  private apiService = inject(ApiService);

  title = signal('');
  content = signal('');
  spaceKey = signal('');
  files = signal<File[]>([]);
  descriptions = signal<string[]>([]);
  attachments = signal<Attachment[]>([]);
  busy = signal(false);
  suggestions = signal<string[]>([]);
  pageId = signal<number | null>(null);
  scheduleId = signal<number | null>(null);

  canUpload = computed(() => this.files().length > 0);

  constructor() {
    // Load default space from backend configuration
    firstValueFrom(this.apiService.getConfig()).then(config => {
      if (config?.defaultSpace) {
        this.spaceKey.set(config.defaultSpace);
      }
    }).catch(err => {
      console.error('Failed to load config:', err);
      // Fallback to empty string - backend will use default
    });
  }

  onFiles(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.files.set(Array.from(input.files));
      this.descriptions.set(new Array(input.files.length).fill(''));
    }
  }

  updateDescription(index: number, value: string) {
    const descriptions = [...this.descriptions()];
    descriptions[index] = value;
    this.descriptions.set(descriptions);
  }

  async uploadAll() {
    if (this.files().length === 0) return;
    this.busy.set(true);
    try {
      const uploaded: Attachment[] = [];
      for (let i = 0; i < this.files().length; i++) {
        const file = this.files()[i];
        const description = this.descriptions()[i];
        const result = await firstValueFrom(this.apiService.uploadAttachment(file, description || undefined));
        if (result) {
          uploaded.push(result);
        }
      }
      this.attachments.update(prev => [...prev, ...uploaded]);
      this.files.set([]);
      this.descriptions.set([]);
    } catch (e) {
      console.error(e);
      alert('Upload failed');
    } finally {
      this.busy.set(false);
    }
  }

  async improvContent() {
    this.busy.set(true);
    try {
      const result = await firstValueFrom(this.apiService.improveContent(this.content()));
      if (result) {
        this.suggestions.set(result.suggestions ?? []);
      }
    } catch (e) {
      console.error(e);
      alert('Content improvement failed');
    } finally {
      this.busy.set(false);
    }
  }

  async createPage() {
    this.busy.set(true);
    try {
      const result = await firstValueFrom(this.apiService.createPage(
        this.title(),
        this.content(),
        this.spaceKey(),
        this.attachments().map(a => a.id)
      ));
      if (result) {
        this.pageId.set(result.id);
        alert('Page created');
      }
    } catch (e) {
      console.error(e);
      alert('Page creation failed');
    } finally {
      this.busy.set(false);
    }
  }

  async publishNow() {
    const pageId = this.pageId();
    if (!pageId) {
      alert('Create a page first');
      return;
    }
    this.busy.set(true);
    try {
      const result = await firstValueFrom(this.apiService.publishNow(pageId));
      if (result) {
        alert(`Published with status: ${result.status}`);
      }
    } catch (e) {
      console.error(e);
      alert('Publish failed');
    } finally {
      this.busy.set(false);
    }
  }

  async schedule() {
    const pageId = this.pageId();
    if (!pageId) {
      alert('Create a page first');
      return;
    }
    this.busy.set(true);
    try {
      const result = await firstValueFrom(this.apiService.schedulePage(pageId));
      if (result) {
        this.scheduleId.set(result.id);
        alert('Scheduled');
      }
    } catch (e) {
      console.error(e);
      alert('Schedule failed');
    } finally {
      this.busy.set(false);
    }
  }
}
```

## Key Design Decisions

### Angular Signals
Using Angular's new signals API for reactive state management:
```typescript
title = signal('');
content = signal('');
busy = signal(false);
attachments = signal<Attachment[]>([]);
```

### Computed Values
```typescript
canUpload = computed(() => this.files().length > 0);
```

### Change Detection
```typescript
changeDetection: ChangeDetectionStrategy.OnPush
```
Using OnPush for better performance with signals.

### Control Flow Syntax
Using Angular 17+ control flow syntax:
```html
@if (suggestions().length > 0) {
  <!-- content -->
}

@for (file of files(); track $index) {
  <!-- content -->
}
```

### Async/Await Pattern
Converting Observables to Promises for cleaner async code:
```typescript
const result = await firstValueFrom(this.apiService.createPage(...));
```

## Component Features

### Page Creation Section
- **Title input**: Required field for page title
- **Space key input**: Optional, defaults to backend config
- **Content textarea**: Main content area
- **Action buttons**:
  - Improve content (AI suggestions)
  - Create page
  - Publish now (requires created page)
  - Schedule (requires created page)

### Attachments Section
- **File input**: Multiple file selection
- **Description inputs**: Optional description per file
- **Upload button**: Uploads all selected files
- **Attached files list**: Shows uploaded attachments

### Content Suggestions
- Displayed after "Improve content" is clicked
- Clickable suggestions replace content

## User Flow
```
1. Enter title and content
2. (Optional) Upload attachments with descriptions
3. Click "Create page" → Page saved to database
4. Click "Publish now" → Immediately publish to Confluence
   OR
   Click "Schedule" → Add to publication queue
```

## Verification
- Form inputs work correctly
- File uploads complete successfully
- Page creation returns page ID
- Publish and Schedule buttons enabled after page creation
- Content suggestions are clickable
- Busy state disables buttons during operations
