# Prompt 12: Frontend Schedules Component

## Context
Continue building the Confluence Publisher application. Create the Schedules page component that displays all publication schedules with auto-refresh functionality.

## Requirements

### Component Location
Create the component in `src/app/pages/schedules/schedules.component.ts`

### Schedules Component Implementation
Create `src/app/pages/schedules/schedules.component.ts`:
```typescript
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
                <td class="p-2 border-b">{{ row.pageId }}</td>
                <td class="p-2 border-b">{{ row.status }}</td>
                <td class="p-2 border-b">{{ formatDate(row.scheduledAt) }}</td>
                <td class="p-2 border-b">{{ row.attemptCount }}</td>
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
```

## Key Design Decisions

### Auto-Refresh
The component automatically refreshes the schedule list every 5 seconds:
```typescript
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
```

### SSR Safety
Check for `window` existence to support server-side rendering:
```typescript
if (typeof window !== 'undefined') {
  this.intervalId = window.setInterval(...);
}
```

### Lifecycle Management
- **OnInit**: Load data and start interval
- **OnDestroy**: Clear interval to prevent memory leaks

### Table Styling
Using TailwindCSS for a clean, responsive table:
- Alternating row colors with `$even` variable
- Horizontal scroll on small screens with `overflow-x-auto`
- Consistent padding and borders

### Date Formatting
```typescript
formatDate(dateString: string): string {
  return new Date(dateString).toLocaleString();
}
```
Converts ISO date strings to localized format.

## Component Features

### Header Section
- **Title**: "Publication Schedules"
- **Refresh button**: Manual refresh, disabled during loading

### Data Table
| Column | Description |
|--------|-------------|
| ID | Schedule ID |
| Page ID | Associated page ID |
| Status | queued, posted, or failed |
| Scheduled | Formatted date/time |
| Attempts | Number of publish attempts |

### Status Values
- **queued**: Waiting to be processed
- **posted**: Successfully published
- **failed**: Publication failed (check logs)

## UI Layout
```
┌─────────────────────────────────────────────────────────┐
│  Publication Schedules                      [Refresh]    │
├─────────────────────────────────────────────────────────┤
│  ID  │  Page ID  │  Status  │  Scheduled      │ Attempts│
├─────────────────────────────────────────────────────────┤
│  1   │  5        │  posted  │  12/8/24, 3:00  │  1      │
│  2   │  6        │  queued  │  12/8/24, 4:00  │  0      │
│  3   │  7        │  failed  │  12/8/24, 2:00  │  3      │
└─────────────────────────────────────────────────────────┘
```

## Integration with Backend Scheduler
The schedules displayed here are processed by the backend `PageScheduler`:
1. User creates a page and clicks "Schedule"
2. Schedule appears in this table with status "queued"
3. Backend scheduler picks up queued schedules
4. Status changes to "posted" or "failed"
5. This component auto-refreshes to show updates

## Verification
- Table loads on component initialization
- Auto-refresh updates data every 5 seconds
- Manual refresh button works
- Dates are formatted correctly
- Alternating row colors display properly
- No memory leaks (interval cleared on destroy)
