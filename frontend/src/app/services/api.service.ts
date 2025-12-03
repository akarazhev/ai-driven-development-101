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
  page_id: number;
  status: string;
  scheduled_at: string;
  attempt_count: number;
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

  createPage(title: string, content: string, spaceKey: string, attachmentIds: number[], parentPageId?: number) {
    return this.http.post<PageResponse>(this.api('/pages'), { 
      title, 
      content, 
      spaceKey,
      parentPageId,
      attachment_ids: attachmentIds 
    });
  }

  publishNow(pageId: number) {
    return this.http.post<PublishResponse>(this.api('/confluence/publish'), { page_id: pageId });
  }

  schedulePage(pageId: number) {
    return this.http.post<Schedule>(this.api('/schedules'), { page_id: pageId });
  }

  getSchedules() {
    return this.http.get<Schedule[]>(this.api('/schedules'));
  }
}
