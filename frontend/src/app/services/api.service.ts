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
