# Angular Material Setup Guide

## Status

✅ **Angular Material is already installed!**

The following packages are in `package.json`:
- `@angular/material@^20.0.0`
- `@angular/cdk@^20.0.0`

Dependencies have been installed with `npm install`.

## Using Angular CLI Commands

Since `ng` is not globally installed, use one of these methods:

### Option 1: Use npx (Recommended)
```powershell
npx ng add @angular/material
```

### Option 2: Use npm scripts
```powershell
npm run ng -- add @angular/material
```

### Option 3: Install Angular CLI globally (Optional)
```powershell
npm install -g @angular/cli
```

## Manual Material Setup

If you prefer to set up Material manually (since it's already installed), follow these steps:

### 1. Import Material Modules

Update `src/main.ts` or create a separate module file:

```typescript
import { bootstrapApplication } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    provideAnimations() // Add this for Material animations
  ]
}).catch(err => console.error(err));
```

### 2. Add Material Theme

Create or update `src/styles.css`:

```css
@import '@angular/material/prebuilt-themes/indigo-pink.css';
/* Or use a custom theme */
```

Available prebuilt themes:
- `indigo-pink.css`
- `deeppurple-amber.css`
- `pink-bluegrey.css`
- `purple-green.css`

### 3. Import Material Components in Your Components

Example in `compose.component.ts`:

```typescript
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-compose',
  imports: [
    CommonModule, 
    FormsModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatCardModule
  ],
  // ...
})
```

### 4. Use Material Components

Replace basic HTML with Material components:

```html
<mat-card>
  <mat-card-header>
    <mat-card-title>Create Confluence Page</mat-card-title>
  </mat-card-header>
  <mat-card-content>
    <mat-form-field>
      <mat-label>Page Title</mat-label>
      <input matInput [(ngModel)]="title">
    </mat-form-field>
    
    <button mat-raised-button color="primary" (click)="createPage()">
      Create Page
    </button>
  </mat-card-content>
</mat-card>
```

## Quick Setup Script

If you want to use the Angular CLI schematics, run:

```powershell
npx ng add @angular/material
```

This will:
- Ask you to choose a theme
- Set up animations
- Add Material imports
- Configure the project

## Material Icons (Optional)

To use Material Icons, add to `index.html`:

```html
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
```

Or use Angular Material's icon font:

```typescript
import { MatIconModule } from '@angular/material/icon';
```

## Next Steps

1. ✅ Dependencies installed
2. ⏭️ Add Material theme to `styles.css`
3. ⏭️ Import `provideAnimations()` in `main.ts`
4. ⏭️ Import Material modules in components
5. ⏭️ Replace HTML elements with Material components

## Resources

- [Angular Material Documentation](https://material.angular.io/)
- [Material Design Guidelines](https://material.io/design)
- [Angular Material Components](https://material.angular.io/components/categories)

