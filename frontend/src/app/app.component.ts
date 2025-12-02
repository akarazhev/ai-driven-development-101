import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="min-h-screen flex flex-col">
      <header class="bg-white border-b">
        <div class="max-w-5xl mx-auto px-4 py-3 flex items-center justify-between">
          <h1 class="text-lg font-semibold">Social Media Automation</h1>
          <nav class="flex gap-4 text-sm">
            <a routerLink="/" routerLinkActive="text-blue-600 font-medium" 
               [routerLinkActiveOptions]="{exact: true}"
               class="text-gray-600 hover:text-blue-600">
              Compose
            </a>
            <a routerLink="/schedules" routerLinkActive="text-blue-600 font-medium"
               class="text-gray-600 hover:text-blue-600">
              Schedules
            </a>
          </nav>
        </div>
      </header>
      <main class="flex-1 max-w-5xl mx-auto px-4 py-6">
        <router-outlet></router-outlet>
      </main>
      <footer class="border-t bg-white text-xs text-gray-500">
        <div class="max-w-5xl mx-auto px-4 py-3">© {{ currentYear }} Social App</div>
      </footer>
    </div>
  `
})
export class AppComponent {
  currentYear = new Date().getFullYear();
}
