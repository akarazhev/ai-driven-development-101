# Prompt 09: Frontend Setup and Routing

## Role
You are an expert front-end engineer.

## Task
Set up the Angular 20 frontend with standalone components, routing, and TailwindCSS styling.

## Requirements

### Main Entry Point (main.ts)
- Bootstrap AppComponent using `bootstrapApplication`
- Provide router with routes
- Provide HttpClient

### Routes (app.routes.ts)
| Path | Component | Loading |
|------|-----------|---------|
| `/` | ComposeComponent | Lazy loaded |
| `/schedules` | SchedulesComponent | Lazy loaded |

### App Component (app.component.ts)
A standalone component with inline template containing:

**Layout Structure**:
- Header with app title "Confluence Publisher" and navigation links
- Main content area with `<router-outlet>`
- Footer with copyright

**Navigation**:
- Link to "/" labeled "Compose"
- Link to "/schedules" labeled "Schedules"
- Use `routerLinkActive` for active state styling

**Styling** (TailwindCSS):
- Min-height screen, flex column layout
- Header: white background, border bottom
- Content: max-width container, centered
- Footer: border top, small text

### Index HTML
- Standard HTML5 doctype
- Title: "Confluence Publisher"
- Body with `bg-gray-50` class
- `<app-root>` element

### Global Styles (styles.css)
- Import Tailwind base, components, utilities
- Set color-scheme to light

### Environment Files
**environment.ts** (development):
- apiBase: 'http://localhost:8080'
- production: false

**environment.prod.ts** (production):
- apiBase: 'http://localhost:8080'
- production: true

### TypeScript Configuration
- Strict mode enabled
- ES2022 target
- Bundler module resolution
- Angular strict templates enabled

## Design Guidelines
- Use standalone components (no NgModules)
- Use inline templates for simple components
- Use TailwindCSS utility classes
- Lazy load page components for code splitting

## Application Layout
```
┌─────────────────────────────────────────────┐
│  Confluence Publisher    [Compose] [Schedules] │
├─────────────────────────────────────────────┤
│                                             │
│            <router-outlet>                  │
│                                             │
├─────────────────────────────────────────────┤
│  © 2024 Confluence Publisher                │
└─────────────────────────────────────────────┘
```

## Verification Criteria
- App starts with `npm start`
- Navigation works between pages
- TailwindCSS classes applied
- No console errors
