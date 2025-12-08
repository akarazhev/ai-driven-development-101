import { Component, inject, signal, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatChipsModule } from '@angular/material/chips';
import { ErrorService } from './services/error.service';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet, 
    RouterLink, 
    RouterLinkActive, 
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatSnackBarModule,
    MatChipsModule
  ],
  template: `
    <div class="app-container">
      <mat-toolbar color="primary" class="app-toolbar">
        <mat-icon class="toolbar-icon">description</mat-icon>
        <span class="toolbar-title">Confluence Publisher</span>
        <nav class="toolbar-nav">
          <button mat-button 
                  routerLink="/" 
                  routerLinkActive="mat-accent"
                  [routerLinkActiveOptions]="{exact: true}"
                  class="nav-button">
            <mat-icon class="button-icon">edit</mat-icon>
            <span>Compose</span>
          </button>
          <button mat-button 
                  routerLink="/schedules" 
                  routerLinkActive="mat-accent"
                  class="nav-button">
            <mat-icon class="button-icon">schedule</mat-icon>
            <span>Schedules</span>
          </button>
        </nav>
      </mat-toolbar>
      
      <!-- New Year Countdown -->
      <div class="countdown-section">
        <div class="countdown-content">
          <h1 class="countdown-title">New Year Countdown</h1>
          <p class="countdown-subtitle">Counting down to a fresh start and new beginnings</p>
          <div class="countdown-year">{{ newYear() }}</div>
          <div class="countdown-boxes">
            <div class="countdown-box">
              <div class="countdown-number">{{ formatNumber(days()) }}</div>
              <div class="countdown-label">DAYS</div>
            </div>
            <div class="countdown-box">
              <div class="countdown-number">{{ formatNumber(hours()) }}</div>
              <div class="countdown-label">HOURS</div>
            </div>
            <div class="countdown-box">
              <div class="countdown-number">{{ formatNumber(minutes()) }}</div>
              <div class="countdown-label">MINUTES</div>
            </div>
            <div class="countdown-box">
              <div class="countdown-number">{{ formatNumber(seconds()) }}</div>
              <div class="countdown-label">SECONDS</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Error Messages -->
      <div class="error-container">
        @for (error of errorService.errors$(); track error.timestamp) {
          <mat-card class="error-card"
                    [style.background-color]="getErrorBackgroundColor(error.type)"
                    [style.color]="getErrorTextColor(error.type)">
            <mat-card-content class="error-content">
              <span class="error-message">{{ error.message }}</span>
              <button mat-icon-button 
                      (click)="errorService.removeError(error)"
                      [style.color]="getErrorTextColor(error.type)"
                      class="error-close-button">
                <mat-icon>close</mat-icon>
              </button>
            </mat-card-content>
          </mat-card>
        }
      </div>
      
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
      
      <mat-toolbar color="primary" class="footer-toolbar">
        <span class="footer-text">© {{ currentYear }} Confluence Publisher</span>
      </mat-toolbar>
    </div>
  `,
  styles: [`
    .app-container {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }

    .app-toolbar {
      display: flex;
      align-items: center;
      padding: 0 16px;
    }

    .toolbar-icon {
      margin-right: 12px;
    }

    .toolbar-title {
      flex: 1;
      font-weight: 500;
    }

    .countdown-section {
      background: linear-gradient(135deg, #0a4d68 0%, #0d7377 100%);
      padding: 40px 24px;
      text-align: center;
      position: relative;
      overflow: hidden;
    }

    .countdown-section::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-image: 
        radial-gradient(circle at 20% 30%, rgba(255, 255, 255, 0.1) 1px, transparent 1px),
        radial-gradient(circle at 60% 70%, rgba(255, 255, 255, 0.1) 1px, transparent 1px),
        radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.1) 1px, transparent 1px),
        radial-gradient(circle at 40% 80%, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
      background-size: 50px 50px, 60px 60px, 40px 40px, 55px 55px;
      background-position: 0 0, 30px 30px, 60px 60px, 90px 90px;
      opacity: 0.3;
      pointer-events: none;
    }

    .countdown-content {
      position: relative;
      z-index: 1;
      max-width: 800px;
      margin: 0 auto;
    }

    .countdown-title {
      font-size: 32px;
      font-weight: 700;
      background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin: 0 0 8px 0;
      text-shadow: 0 0 20px rgba(255, 107, 53, 0.3);
    }

    .countdown-subtitle {
      font-size: 14px;
      color: rgba(255, 255, 255, 0.8);
      margin: 0 0 24px 0;
      font-weight: 300;
    }

    .countdown-year {
      font-size: 72px;
      font-weight: 700;
      background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin: 0 0 32px 0;
      text-shadow: 0 0 30px rgba(255, 107, 53, 0.4);
      letter-spacing: 4px;
    }

    .countdown-boxes {
      display: flex;
      justify-content: center;
      gap: 16px;
      flex-wrap: wrap;
    }

    .countdown-box {
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      border-radius: 12px;
      padding: 20px 24px;
      min-width: 100px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    .countdown-number {
      font-size: 36px;
      font-weight: 700;
      background: linear-gradient(135deg, #ff6b35 0%, #f7931e 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin-bottom: 8px;
      text-shadow: 0 0 20px rgba(255, 107, 53, 0.3);
      font-variant-numeric: tabular-nums;
      min-height: 44px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .countdown-label {
      font-size: 12px;
      font-weight: 600;
      color: rgba(255, 255, 255, 0.9);
      letter-spacing: 1px;
      text-transform: uppercase;
    }

    .toolbar-nav {
      display: flex;
      gap: 4px;
    }

    .nav-button {
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

    .error-container {
      max-width: 1200px;
      width: 100%;
      margin: 0 auto;
      padding: 16px;
    }

    .error-card {
      margin-bottom: 8px;
    }

    .error-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 8px 16px !important;
    }

    .error-message {
      flex: 1;
    }

    .error-close-button {
      margin-left: 8px;
    }

    .main-content {
      flex: 1;
      max-width: 1200px;
      width: 100%;
      margin: 0 auto;
      padding: 16px;
    }

    .footer-toolbar {
      margin-top: auto;
      justify-content: center;
    }

    .footer-text {
      font-size: 12px;
    }
  `]
})
export class AppComponent implements OnInit, OnDestroy {
  errorService = inject(ErrorService);
  currentYear = new Date().getFullYear();
  newYear = signal<number>(0);
  days = signal<number>(0);
  hours = signal<number>(0);
  minutes = signal<number>(0);
  seconds = signal<number>(0);
  private intervalId?: number;

  ngOnInit() {
    this.updateCountdown();
    // Update countdown every second
    if (typeof window !== 'undefined') {
      this.intervalId = window.setInterval(() => this.updateCountdown(), 1000);
    }
  }

  ngOnDestroy() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  private updateCountdown() {
    const now = new Date();
    const currentYear = now.getFullYear();
    const nextNewYear = new Date(currentYear + 1, 0, 1, 0, 0, 0, 0);
    this.newYear.set(currentYear + 1);
    
    const diff = nextNewYear.getTime() - now.getTime();
    
    if (diff > 0) {
      const totalSeconds = Math.floor(diff / 1000);
      const totalMinutes = Math.floor(totalSeconds / 60);
      const totalHours = Math.floor(totalMinutes / 60);
      const totalDays = Math.floor(totalHours / 24);
      
      this.days.set(totalDays);
      this.hours.set(totalHours % 24);
      this.minutes.set(totalMinutes % 60);
      this.seconds.set(totalSeconds % 60);
    } else {
      // New Year has passed, calculate for next year
      const nextYear = new Date(currentYear + 2, 0, 1, 0, 0, 0, 0);
      this.newYear.set(currentYear + 2);
      const diffNext = nextYear.getTime() - now.getTime();
      const totalSeconds = Math.floor(diffNext / 1000);
      const totalMinutes = Math.floor(totalSeconds / 60);
      const totalHours = Math.floor(totalMinutes / 60);
      const totalDays = Math.floor(totalHours / 24);
      
      this.days.set(totalDays);
      this.hours.set(totalHours % 24);
      this.minutes.set(totalMinutes % 60);
      this.seconds.set(totalSeconds % 60);
    }
  }

  formatNumber(num: number): string {
    return num.toString().padStart(2, '0');
  }
  
  getErrorBackgroundColor(type: 'error' | 'warning' | 'info'): string {
    switch (type) {
      case 'error':
        return '#ffebee';
      case 'warning':
        return '#fff3e0';
      case 'info':
        return '#e3f2fd';
      default:
        return '#f5f5f5';
    }
  }
  
  getErrorTextColor(type: 'error' | 'warning' | 'info'): string {
    switch (type) {
      case 'error':
        return '#c62828';
      case 'warning':
        return '#e65100';
      case 'info':
        return '#1565c0';
      default:
        return '#424242';
    }
  }
}
