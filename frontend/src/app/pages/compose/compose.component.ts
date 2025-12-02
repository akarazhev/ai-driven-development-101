import { Component, signal, computed, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { ApiService, Media } from '../../services/api.service';

@Component({
  selector: 'app-compose',
  imports: [CommonModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="space-y-8">
      <section class="space-y-3">
        <h2 class="text-base font-semibold">Compose</h2>
        <textarea
          class="w-full border rounded p-3 focus:outline-none focus:ring"
          rows="4"
          placeholder="Write your post..."
          [value]="text()"
          (input)="text.set($any($event.target).value)"
        ></textarea>
        <div class="flex gap-2">
          <button 
            (click)="makeVariants()" 
            [disabled]="busy() || !text()" 
            class="px-3 py-2 bg-blue-600 text-white rounded disabled:opacity-50">
            Generate variants
          </button>
          <button 
            (click)="createPost()" 
            [disabled]="busy() || !text()" 
            class="px-3 py-2 bg-green-600 text-white rounded disabled:opacity-50">
            Create post
          </button>
          <button 
            (click)="publishNow()" 
            [disabled]="busy() || !postId()" 
            class="px-3 py-2 bg-purple-600 text-white rounded disabled:opacity-50">
            Publish now
          </button>
          <button 
            (click)="schedule()" 
            [disabled]="busy() || !postId()" 
            class="px-3 py-2 bg-amber-600 text-white rounded disabled:opacity-50">
            Schedule
          </button>
        </div>
        @if (variants().length > 0) {
          <div class="bg-white border rounded p-3">
            <h3 class="font-medium mb-2">Variants</h3>
            <ul class="list-disc pl-5 space-y-1">
              @for (variant of variants(); track $index) {
                <li>
                  <button 
                    class="text-blue-600 hover:underline" 
                    (click)="text.set(variant)">
                    {{ variant }}
                  </button>
                </li>
              }
            </ul>
          </div>
        }
      </section>

      <section class="space-y-3">
        <h2 class="text-base font-semibold">Media</h2>
        <input 
          type="file" 
          multiple 
          accept="image/*" 
          (change)="onFiles($event)"
          #fileInput
        />
        @if (files().length > 0) {
          <div class="space-y-2">
            @for (file of files(); track $index; let idx = $index) {
              <div class="flex items-center gap-2 text-sm">
                <div class="text-gray-600">{{ file.name }}</div>
                <input 
                  #altInput
                  class="border rounded p-1 flex-1" 
                  placeholder="Alt text (optional)"
                  [value]="altTexts()[idx]"
                  (input)="updateAltText(idx, $any($event.target).value)"
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
        @if (media().length > 0) {
          <div class="bg-white border rounded p-3">
            <h3 class="font-medium mb-2">Attached</h3>
            <ul class="list-disc pl-5 text-sm space-y-1">
              @for (m of media(); track m.id) {
                <li>
                  {{ m.filename }} 
                  @if (m.alt_text) {
                    (alt: {{ m.alt_text }})
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

  text = signal('');
  files = signal<File[]>([]);
  altTexts = signal<string[]>([]);
  media = signal<Media[]>([]);
  busy = signal(false);
  variants = signal<string[]>([]);
  postId = signal<number | null>(null);
  scheduleId = signal<number | null>(null);

  canUpload = computed(() => this.files().length > 0);

  onFiles(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.files.set(Array.from(input.files));
      this.altTexts.set(new Array(input.files.length).fill(''));
    }
  }

  updateAltText(index: number, value: string) {
    const altTexts = [...this.altTexts()];
    altTexts[index] = value;
    this.altTexts.set(altTexts);
  }

  async uploadAll() {
    if (this.files().length === 0) return;
    this.busy.set(true);
    try {
      const uploaded: Media[] = [];
      for (let i = 0; i < this.files().length; i++) {
        const file = this.files()[i];
        const altText = this.altTexts()[i];
        const result = await firstValueFrom(this.apiService.uploadMedia(file, altText || undefined));
        if (result) {
          uploaded.push(result);
        }
      }
      this.media.update(prev => [...prev, ...uploaded]);
      this.files.set([]);
      this.altTexts.set([]);
    } catch (e) {
      console.error(e);
      alert('Upload failed');
    } finally {
      this.busy.set(false);
    }
  }

  async makeVariants() {
    this.busy.set(true);
    try {
      const result = await firstValueFrom(this.apiService.generateVariants(this.text()));
      if (result) {
        this.variants.set(result.variants ?? []);
      }
    } catch (e) {
      console.error(e);
      alert('Variant generation failed');
    } finally {
      this.busy.set(false);
    }
  }

  async createPost() {
    this.busy.set(true);
    try {
      const result = await firstValueFrom(this.apiService.createPost(
        this.text(),
        this.media().map(m => m.id)
      ));
      if (result) {
        this.postId.set(result.id);
        alert('Post created');
      }
    } catch (e) {
      console.error(e);
      alert('Post creation failed');
    } finally {
      this.busy.set(false);
    }
  }

  async publishNow() {
    const postId = this.postId();
    if (!postId) {
      alert('Create a post first');
      return;
    }
    this.busy.set(true);
    try {
      const result = await firstValueFrom(this.apiService.publishNow(postId));
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
    const postId = this.postId();
    if (!postId) {
      alert('Create a post first');
      return;
    }
    this.busy.set(true);
    try {
      const result = await firstValueFrom(this.apiService.schedulePost(postId));
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
