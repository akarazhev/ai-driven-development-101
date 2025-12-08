import { Component, signal, inject, ChangeDetectionStrategy, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { firstValueFrom } from 'rxjs';
import { ApiService, Schedule } from '../../services/api.service';
import { ErrorService } from '../../services/error.service';

@Component({
  selector: 'app-schedules',
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="schedules-container">
      <mat-card>
        <mat-card-header class="schedules-header">
          <mat-card-title>Publication Schedules</mat-card-title>
          <button mat-raised-button 
                  color="primary"
                  (click)="load()" 
                  [disabled]="busy()"
                  class="refresh-button">
            @if (busy()) {
              <mat-spinner diameter="16" class="button-spinner"></mat-spinner>
            } @else {
              <mat-icon class="button-icon">refresh</mat-icon>
            }
            Refresh
          </button>
        </mat-card-header>
        <mat-card-content>
          <table mat-table [dataSource]="rows()" class="schedules-table">
            <ng-container matColumnDef="id">
              <th mat-header-cell *matHeaderCellDef>ID</th>
              <td mat-cell *matCellDef="let row">{{ row.id }}</td>
            </ng-container>
            
            <ng-container matColumnDef="pageId">
              <th mat-header-cell *matHeaderCellDef>Page ID</th>
              <td mat-cell *matCellDef="let row">{{ row.pageId }}</td>
            </ng-container>
            
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef>Status</th>
              <td mat-cell *matCellDef="let row">
                <mat-chip [color]="getStatusColor(row.status)">
                  {{ row.status }}
                </mat-chip>
              </td>
            </ng-container>
            
            <ng-container matColumnDef="scheduledAt">
              <th mat-header-cell *matHeaderCellDef>Scheduled</th>
              <td mat-cell *matCellDef="let row">{{ formatDate(row.scheduledAt) }}</td>
            </ng-container>
            
            <ng-container matColumnDef="attemptCount">
              <th mat-header-cell *matHeaderCellDef>Attempts</th>
              <td mat-cell *matCellDef="let row">{{ row.attemptCount }}</td>
            </ng-container>
            
            <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
            
            @if (rows().length === 0) {
              <tr class="mat-row">
                <td class="mat-cell empty-message" [attr.colspan]="displayedColumns.length">
                  No schedules found
                </td>
              </tr>
            }
          </table>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .schedules-container {
      display: flex;
      flex-direction: column;
      gap: 16px;
      padding: 16px;
    }

    .schedules-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .refresh-button {
      display: flex;
      align-items: center;
      gap: 4px;
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

    .schedules-table {
      width: 100%;
    }

    .empty-message {
      text-align: center;
      padding: 24px;
    }

    @media (max-width: 768px) {
      .schedules-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
      }

      .refresh-button {
        width: 100%;
      }
    }
  `]
})
export class SchedulesComponent implements OnInit, OnDestroy {
  private apiService = inject(ApiService);
  private errorService = inject(ErrorService);
  private intervalId?: number;

  rows = signal<Schedule[]>([]);
  busy = signal(false);
  displayedColumns: string[] = ['id', 'pageId', 'status', 'scheduledAt', 'attemptCount'];

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
      this.errorService.handleError(e, 'Failed to load schedules');
    } finally {
      this.busy.set(false);
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString();
  }

  getStatusColor(status: string): 'primary' | 'accent' | 'warn' | undefined {
    switch (status.toLowerCase()) {
      case 'queued':
        return 'primary';
      case 'published':
        return 'accent';
      case 'failed':
        return 'warn';
      default:
        return undefined;
    }
  }
}
