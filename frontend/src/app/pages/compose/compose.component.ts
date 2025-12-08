import { Component, signal, computed, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { firstValueFrom } from 'rxjs';
import { ApiService, Attachment } from '../../services/api.service';
import { ErrorService } from '../../services/error.service';

@Component({
  selector: 'app-compose',
  imports: [
    CommonModule, 
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="compose-container">
      <mat-card class="page-card">
        <mat-card-header class="card-header">
          <mat-card-title class="card-title">
            <mat-icon class="title-icon">description</mat-icon>
            Create Confluence Page
          </mat-card-title>
        </mat-card-header>
        <mat-card-content class="page-card-content">
          <div class="form-group">
            <label class="form-label" for="pageTitle">Page Title</label>
            <input
              id="pageTitle"
              type="text"
              class="form-control"
              [value]="title()"
              (input)="title.set($any($event.target).value)"
              placeholder="Enter page title..." />
          </div>
          
          <div class="form-group">
            <label class="form-label" for="spaceKey">Space Key</label>
            <input
              id="spaceKey"
              type="text"
              class="form-control"
              [value]="spaceKey()"
              (input)="spaceKey.set($any($event.target).value)"
              placeholder="e.g., SPGAC, DEV" />
          </div>
          
          <div class="form-group">
            <label class="form-label" for="pageContent">Page Content</label>
            <textarea
              id="pageContent"
              class="form-control form-textarea"
              rows="10"
              [value]="content()"
              (input)="content.set($any($event.target).value)"
              placeholder="Enter page content..."></textarea>
          </div>
          
          <div class="action-buttons">
            <button mat-raised-button 
                    color="primary"
                    (click)="improvContent()" 
                    [disabled]="busy() || !content()"
                    class="action-button">
              @if (busy()) {
                <mat-spinner diameter="18" class="button-spinner"></mat-spinner>
                Processing...
              } @else {
                <ng-container>
                  <mat-icon class="button-icon">auto_fix_high</mat-icon>
                  Improve Content
                </ng-container>
              }
            </button>
            
            <button mat-raised-button 
                    color="accent"
                    (click)="createPage()" 
                    [disabled]="busy() || !title() || !content() || !spaceKey()"
                    class="action-button">
              @if (busy()) {
                <mat-spinner diameter="18" class="button-spinner"></mat-spinner>
                Creating...
              } @else {
                <ng-container>
                  <mat-icon class="button-icon">add</mat-icon>
                  Create Page
                </ng-container>
              }
            </button>
            
            <button mat-raised-button 
                    color="primary"
                    (click)="publishNow()" 
                    [disabled]="busy() || !pageId()"
                    class="action-button">
              @if (busy()) {
                <mat-spinner diameter="18" class="button-spinner"></mat-spinner>
                Publishing...
              } @else {
                <ng-container>
                  <mat-icon class="button-icon">publish</mat-icon>
                  Publish Now
                </ng-container>
              }
            </button>
            
            <button mat-raised-button 
                    (click)="schedule()" 
                    [disabled]="busy() || !pageId()"
                    class="action-button">
              @if (busy()) {
                <mat-spinner diameter="18" class="button-spinner"></mat-spinner>
                Scheduling...
              } @else {
                <ng-container>
                  <mat-icon class="button-icon">schedule</mat-icon>
                  Schedule
                </ng-container>
              }
            </button>
          </div>
          
          @if (suggestions().length > 0) {
            <div class="suggestions-section">
              <h3 class="section-title">
                <mat-icon class="section-icon">lightbulb</mat-icon>
                Content Suggestions
              </h3>
              <div class="suggestions-list">
                @for (suggestion of suggestions(); track $index) {
                  <button mat-stroked-button 
                          (click)="content.set(suggestion)"
                          class="suggestion-item">
                    {{ suggestion.substring(0, 120) }}{{ suggestion.length > 120 ? '...' : '' }}
                  </button>
                }
              </div>
            </div>
          }
        </mat-card-content>
      </mat-card>

      <mat-card class="attachments-card">
        <mat-card-header class="card-header">
          <mat-card-title class="card-title">
            <mat-icon class="title-icon">attach_file</mat-icon>
            Attachments
          </mat-card-title>
        </mat-card-header>
        <mat-card-content class="attachments-content">
          <div class="file-select-container">
            <input 
              type="file" 
              multiple 
              (change)="onFiles($event)"
              #fileInput
              class="file-input"
              id="fileInput" />
            <button mat-raised-button 
                    color="primary"
                    (click)="fileInput.click()"
                    class="select-files-button">
              <ng-container>
                <mat-icon class="button-icon">attach_file</mat-icon>
                Select Files
              </ng-container>
            </button>
          </div>
          
          @if (files().length > 0) {
            <div class="files-list">
              @for (file of files(); track $index; let idx = $index) {
                <div class="file-item">
                  <mat-icon class="file-icon">insert_drive_file</mat-icon>
                  <span class="file-name">{{ file.name }}</span>
                  <input 
                    type="text"
                    class="file-description-input"
                    [value]="descriptions()[idx]"
                    (input)="updateDescription(idx, $any($event.target).value)"
                    placeholder="Description (optional)" />
                </div>
              }
              <button mat-raised-button 
                      color="accent"
                      (click)="uploadAll()" 
                      [disabled]="busy() || !canUpload()"
                      class="upload-button">
                @if (busy()) {
                  <mat-spinner diameter="18" class="button-spinner"></mat-spinner>
                  Uploading...
                } @else {
                  <ng-container>
                    <mat-icon class="button-icon">cloud_upload</mat-icon>
                    Upload Files
                  </ng-container>
                }
              </button>
            </div>
          }
          
          @if (attachments().length > 0) {
            <div class="attached-files-section">
              <h3 class="section-title">
                <mat-icon class="section-icon">check_circle</mat-icon>
                Attached Files
              </h3>
              <div class="attached-files-list">
                @for (att of attachments(); track att.id) {
                  <div class="attached-file-item">
                    <mat-icon class="attached-file-icon">attach_file</mat-icon>
                    <div class="attached-file-info">
                      <div class="attached-file-name">{{ att.filename }}</div>
                      @if (att.description) {
                        <div class="attached-file-desc">{{ att.description }}</div>
                      }
                    </div>
                  </div>
                }
              </div>
            </div>
          }
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .compose-container {
      display: flex;
      flex-direction: column;
      gap: 24px;
      padding: 24px;
      max-width: 900px;
      margin: 0 auto;
    }

    .page-card,
    .attachments-card {
      width: 100%;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .card-header {
      padding: 20px 24px 16px 24px;
      border-bottom: 1px solid rgba(0, 0, 0, 0.12);
    }

    .card-title {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 20px;
      font-weight: 500;
      margin: 0;
    }

    .title-icon {
      color: var(--mat-sys-primary);
    }

    .page-card-content {
      display: flex;
      flex-direction: column;
      gap: 24px;
      padding: 24px;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .form-label {
      font-size: 14px;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.87);
      margin-bottom: 4px;
    }

    .form-control {
      width: 100%;
      padding: 12px 16px;
      font-size: 16px;
      border: 1px solid rgba(0, 0, 0, 0.23);
      border-radius: 4px;
      box-sizing: border-box;
      font-family: Roboto, "Helvetica Neue", sans-serif;
      transition: border-color 0.2s, box-shadow 0.2s;
    }

    .form-control:focus {
      outline: none;
      border-color: var(--mat-sys-primary);
      border-width: 2px;
      padding: 11px 15px;
    }

    .form-control:hover:not(:focus) {
      border-color: rgba(0, 0, 0, 0.87);
    }

    .form-textarea {
      resize: vertical;
      min-height: 200px;
      font-family: inherit;
      line-height: 1.5;
    }


    .action-buttons {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
      margin-top: 8px;
    }

    .action-button {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 20px;
      font-size: 14px;
      font-weight: 500;
      text-transform: none;
      border-radius: 4px;
    }

    .button-icon {
      font-size: 20px;
      width: 20px;
      height: 20px;
      line-height: 20px;
    }

    .button-spinner {
      display: inline-block;
      margin-right: 8px;
    }

    .suggestions-section {
      margin-top: 8px;
      padding: 20px;
      background-color: #f5f5f5;
      border-radius: 8px;
    }

    .section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 500;
      margin: 0 0 16px 0;
      color: rgba(0, 0, 0, 0.87);
    }

    .section-icon {
      font-size: 20px;
      width: 20px;
      height: 20px;
      color: var(--mat-sys-primary);
    }

    .suggestions-list {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .suggestion-item {
      text-align: left;
      justify-content: flex-start;
      padding: 12px 16px;
      white-space: normal;
      word-wrap: break-word;
    }

    .attachments-content {
      display: flex;
      flex-direction: column;
      gap: 20px;
      padding: 24px;
    }

    .file-select-container {
      display: flex;
    }

    .file-input {
      display: none;
    }

    .select-files-button {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 20px;
      font-size: 14px;
      font-weight: 500;
      text-transform: none;
    }

    .files-list {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .file-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px;
      background-color: #fafafa;
      border-radius: 4px;
      border: 1px solid rgba(0, 0, 0, 0.12);
    }

    .file-icon {
      color: rgba(0, 0, 0, 0.54);
      font-size: 24px;
      width: 24px;
      height: 24px;
    }

    .file-name {
      flex: 1;
      min-width: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 14px;
    }

    .file-description-input {
      width: 250px;
      padding: 8px 12px;
      font-size: 14px;
      border: 1px solid rgba(0, 0, 0, 0.23);
      border-radius: 4px;
      box-sizing: border-box;
    }

    .file-description-input:focus {
      outline: none;
      border-color: var(--mat-sys-primary);
      border-width: 2px;
      padding: 7px 11px;
    }

    .upload-button {
      display: flex;
      align-items: center;
      gap: 8px;
      align-self: flex-start;
      padding: 10px 20px;
      font-size: 14px;
      font-weight: 500;
      text-transform: none;
    }

    .attached-files-section {
      margin-top: 8px;
      padding: 20px;
      background-color: #f5f5f5;
      border-radius: 8px;
    }

    .attached-files-list {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .attached-file-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px;
      background-color: white;
      border-radius: 4px;
      border: 1px solid rgba(0, 0, 0, 0.12);
    }

    .attached-file-icon {
      color: var(--mat-sys-primary);
      font-size: 24px;
      width: 24px;
      height: 24px;
    }

    .attached-file-info {
      flex: 1;
    }

    .attached-file-name {
      font-size: 14px;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.87);
    }

    .attached-file-desc {
      font-size: 12px;
      color: rgba(0, 0, 0, 0.54);
      margin-top: 4px;
    }

    @media (max-width: 768px) {
      .compose-container {
        padding: 16px;
      }

      .page-card-content,
      .attachments-content {
        padding: 16px;
      }

      .file-item {
        flex-direction: column;
        align-items: stretch;
        gap: 8px;
      }

      .file-description-input {
        width: 100%;
      }

      .action-buttons {
        flex-direction: column;
      }

      .action-button {
        width: 100%;
        justify-content: center;
      }
    }
  `]
})
export class ComposeComponent {
  private apiService = inject(ApiService);
  private errorService = inject(ErrorService);

  title = signal('');
  content = signal('');
  spaceKey = signal('SPGAC');
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
      this.errorService.addError('Files uploaded successfully', 'info');
    } catch (e) {
      this.errorService.handleError(e, 'Failed to upload files');
    } finally {
      this.busy.set(false);
    }
  }

  async improvContent() {
    if (!this.content().trim()) {
      this.errorService.addError('Please enter some content first', 'warning');
      return;
    }
    this.busy.set(true);
    try {
      const result = await firstValueFrom(this.apiService.improveContent(this.content()));
      if (result && result.suggestions && result.suggestions.length > 0) {
        this.suggestions.set(result.suggestions);
        this.errorService.addError('Content suggestions generated', 'info');
      } else {
        this.errorService.addError('No suggestions available', 'warning');
      }
    } catch (e) {
      this.errorService.handleError(e, 'Failed to improve content');
    } finally {
      this.busy.set(false);
    }
  }

  async createPage() {
    if (!this.title().trim()) {
      this.errorService.addError('Page title is required', 'warning');
      return;
    }
    if (!this.content().trim()) {
      this.errorService.addError('Page content is required', 'warning');
      return;
    }
    if (!this.spaceKey().trim()) {
      this.errorService.addError('Space key is required', 'warning');
      return;
    }
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
        this.errorService.addError('Page created successfully', 'info');
      }
    } catch (e) {
      this.errorService.handleError(e, 'Failed to create page');
    } finally {
      this.busy.set(false);
    }
  }

  async publishNow() {
    const pageId = this.pageId();
    if (!pageId) {
      this.errorService.addError('Please create a page first', 'warning');
      return;
    }
    this.busy.set(true);
    try {
      const result = await firstValueFrom(this.apiService.publishNow(pageId));
      if (result) {
        this.errorService.addError(`Page published successfully with status: ${result.status}`, 'info');
      }
    } catch (e) {
      this.errorService.handleError(e, 'Failed to publish page');
    } finally {
      this.busy.set(false);
    }
  }

  async schedule() {
    const pageId = this.pageId();
    if (!pageId) {
      this.errorService.addError('Please create a page first', 'warning');
      return;
    }
    this.busy.set(true);
    try {
      const result = await firstValueFrom(this.apiService.schedulePage(pageId));
      if (result) {
        this.scheduleId.set(result.id);
        this.errorService.addError('Page scheduled successfully', 'info');
      }
    } catch (e) {
      this.errorService.handleError(e, 'Failed to schedule page');
    } finally {
      this.busy.set(false);
    }
  }
}
