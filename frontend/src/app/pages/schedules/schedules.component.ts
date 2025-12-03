import { Component, signal, inject, ChangeDetectionStrategy, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { ApiService, Schedule } from '../../services/api.service';

@Component({
  selector: 'app-schedules',
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="space-y-3">
      <div class="flex items-center justify-between">
        <h2 class="text-base font-semibold">Publication Schedules</h2>
        <button 
          (click)="load()" 
          [disabled]="busy()" 
          class="px-3 py-2 bg-gray-800 text-white rounded disabled:opacity-50">
          Refresh
        </button>
      </div>
      <div class="bg-white border rounded overflow-x-auto">
        <table class="min-w-full text-sm">
          <thead>
            <tr class="bg-gray-50 text-gray-700">
              <th class="text-left p-2 border-b">ID</th>
              <th class="text-left p-2 border-b">Page ID</th>
              <th class="text-left p-2 border-b">Status</th>
              <th class="text-left p-2 border-b">Scheduled</th>
              <th class="text-left p-2 border-b">Attempts</th>
            </tr>
          </thead>
          <tbody>
            @for (row of rows(); track row.id) {
              <tr [class]="$even ? 'bg-gray-50' : 'bg-white'">
                <td class="p-2 border-b">{{ row.id }}</td>
                <td class="p-2 border-b">{{ row.page_id }}</td>
                <td class="p-2 border-b">{{ row.status }}</td>
                <td class="p-2 border-b">{{ formatDate(row.scheduled_at) }}</td>
                <td class="p-2 border-b">{{ row.attempt_count }}</td>
              </tr>
            }
          </tbody>
        </table>
      </div>
    </div>
  `
})
export class SchedulesComponent implements OnInit, OnDestroy {
  private apiService = inject(ApiService);
  private intervalId?: number;

  rows = signal<Schedule[]>([]);
  busy = signal(false);

  ngOnInit() {
    this.load();
    if (typeof window !== 'undefined') {
      this.intervalId = window.setInterval(() => this.load(), 5000);
    }
  }

  ngOnDestroy() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  async load() {
    this.busy.set(true);
    try {
      const result = await firstValueFrom(this.apiService.getSchedules());
      if (result) {
        this.rows.set(result);
      }
    } catch (e) {
      console.error(e);
      alert('Failed to load schedules');
    } finally {
      this.busy.set(false);
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString();
  }
}
