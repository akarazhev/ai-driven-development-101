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

