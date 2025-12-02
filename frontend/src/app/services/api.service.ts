import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface Media {
  id: number;
  filename: string;
  alt_text?: string;
}

export interface Schedule {
  id: number;
  post_id: number;
  status: string;
  scheduled_at: string;
  attempt_count: number;
}

export interface VariantsResponse {
  variants: string[];
}

export interface PostResponse {
  id: number;
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

  uploadMedia(file: File, altText?: string) {
    const formData = new FormData();
    formData.append('file', file);
    if (altText) {
      formData.append('alt_text', altText);
    }
    return this.http.post<Media>(this.api('/media'), formData);
  }

  generateVariants(message: string) {
    return this.http.post<VariantsResponse>(this.api('/ai/variants'), { message });
  }

  createPost(text: string, mediaIds: number[]) {
    return this.http.post<PostResponse>(this.api('/posts'), { text, media_ids: mediaIds });
  }

  publishNow(postId: number) {
    return this.http.post<PublishResponse>(this.api('/providers/default/publish'), { post_id: postId });
  }

  schedulePost(postId: number) {
    return this.http.post<Schedule>(this.api('/schedules'), { post_id: postId });
  }

  getSchedules() {
    return this.http.get<Schedule[]>(this.api('/schedules'));
  }
}
