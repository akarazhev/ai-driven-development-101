# Prompt 09: Frontend Setup and Routing

## Context
Continue building the Confluence Publisher application. Set up the Angular 20 frontend with standalone components, routing, and TailwindCSS styling.

## Requirements

### Angular Configuration
Use Angular 20 with standalone components (no NgModules).

### Main Entry Point (main.ts)
Create `src/main.ts`:
```typescript
import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient()
  ]
}).catch(err => console.error(err));
```

### Routes Configuration (app.routes.ts)
Create `src/app/app.routes.ts`:
```typescript
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/compose/compose.component').then(m => m.ComposeComponent)
  },
  {
    path: 'schedules',
    loadComponent: () => import('./pages/schedules/schedules.component').then(m => m.SchedulesComponent)
  }
];
```

### App Component (app.component.ts)
Create `src/app/app.component.ts`:
```typescript
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="min-h-screen flex flex-col">
      <header class="bg-white border-b">
        <div class="max-w-5xl mx-auto px-4 py-3 flex items-center justify-between">
          <h1 class="text-lg font-semibold">Confluence Publisher</h1>
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
        <div class="max-w-5xl mx-auto px-4 py-3">© {{ currentYear }} Confluence Publisher</div>
      </footer>
    </div>
  `
})
export class AppComponent {
  currentYear = new Date().getFullYear();
}
```

### Index HTML (index.html)
Create `src/index.html`:
```html
<!doctype html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Confluence Publisher</title>
  </head>
  <body class="bg-gray-50">
    <app-root></app-root>
  </body>
</html>
```

### Global Styles (styles.css)
Create `src/styles.css`:
```css
@tailwind base;
@tailwind components;
@tailwind utilities;

:root {
  color-scheme: light;
}
```

### Environment Configuration
Create `src/environments/environment.ts`:
```typescript
export const environment = {
  apiBase: 'http://localhost:8080',
  production: false
};
```

Create `src/environments/environment.prod.ts`:
```typescript
export const environment = {
  apiBase: 'http://localhost:8080',
  production: true
};
```

### TypeScript Configuration (tsconfig.json)
Create `tsconfig.json`:
```json
{
  "compileOnSave": false,
  "compilerOptions": {
    "outDir": "./dist/out-tsc",
    "forceConsistentCasingInFileNames": true,
    "strict": true,
    "noImplicitOverride": true,
    "noPropertyAccessFromIndexSignature": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true,
    "skipLibCheck": true,
    "esModuleInterop": true,
    "sourceMap": true,
    "declaration": false,
    "experimentalDecorators": true,
    "moduleResolution": "bundler",
    "importHelpers": true,
    "target": "ES2022",
    "module": "ES2022",
    "lib": [
      "ES2022",
      "dom"
    ],
    "paths": {
      "@/*": ["./src/*"]
    }
  },
  "angularCompilerOptions": {
    "enableI18nLegacyMessageIdFormat": false,
    "strictInjectionParameters": true,
    "strictInputAccessModifiers": true,
    "strictTemplates": true
  }
}
```

Create `tsconfig.app.json`:
```json
{
  "extends": "./tsconfig.json",
  "compilerOptions": {
    "outDir": "./out-tsc/app",
    "types": []
  },
  "files": [
    "src/main.ts"
  ],
  "include": [
    "src/**/*.d.ts"
  ]
}
```

### Angular Configuration (angular.json)
Create `angular.json`:
```json
{
  "$schema": "./node_modules/@angular/cli/lib/config/schema.json",
  "version": 1,
  "newProjectRoot": "projects",
  "projects": {
    "confluence-publisher-app": {
      "projectType": "application",
      "schematics": {
        "@schematics/angular:component": {
          "style": "css",
          "standalone": true
        },
        "@schematics/angular:directive": {
          "standalone": true
        },
        "@schematics/angular:pipe": {
          "standalone": true
        }
      },
      "root": "",
      "sourceRoot": "src",
      "prefix": "app",
      "architect": {
        "build": {
          "builder": "@angular-devkit/build-angular:application",
          "options": {
            "outputPath": "dist/confluence-publisher-app",
            "index": "src/index.html",
            "browser": "src/main.ts",
            "polyfills": [
              "zone.js"
            ],
            "tsConfig": "tsconfig.app.json",
            "assets": [
              "src/favicon.ico",
              "src/assets"
            ],
            "styles": [
              "src/styles.css"
            ],
            "scripts": []
          },
          "configurations": {
            "production": {
              "budgets": [
                {
                  "type": "initial",
                  "maximumWarning": "500kB",
                  "maximumError": "1MB"
                },
                {
                  "type": "anyComponentStyle",
                  "maximumWarning": "2kB",
                  "maximumError": "4kB"
                }
              ],
              "outputHashing": "all",
              "fileReplacements": [
                {
                  "replace": "src/environments/environment.ts",
                  "with": "src/environments/environment.prod.ts"
                }
              ]
            },
            "development": {
              "optimization": false,
              "extractLicenses": false,
              "sourceMap": true
            }
          },
          "defaultConfiguration": "production"
        },
        "serve": {
          "builder": "@angular-devkit/build-angular:dev-server",
          "configurations": {
            "production": {
              "buildTarget": "confluence-publisher-app:build:production"
            },
            "development": {
              "buildTarget": "confluence-publisher-app:build:development"
            }
          },
          "defaultConfiguration": "development"
        }
      }
    }
  }
}
```

## Key Design Decisions
1. **Standalone components**: No NgModules, using Angular 20's standalone component architecture
2. **Lazy loading**: Routes use `loadComponent` for code splitting
3. **TailwindCSS**: Utility-first CSS framework for styling
4. **Inline templates**: Components use inline templates for simplicity
5. **Responsive layout**: Max-width container with proper padding
6. **Active route highlighting**: Using `routerLinkActive` for navigation state

## Application Layout
```
┌─────────────────────────────────────────────────────────┐
│  Header: "Confluence Publisher"    [Compose] [Schedules]│
├─────────────────────────────────────────────────────────┤
│                                                          │
│                    <router-outlet>                       │
│                    (Page content)                        │
│                                                          │
├─────────────────────────────────────────────────────────┤
│  Footer: © 2024 Confluence Publisher                     │
└─────────────────────────────────────────────────────────┘
```

## Verification
- Application starts with `npm start`
- Navigation between Compose and Schedules works
- TailwindCSS classes are applied correctly
- No console errors
