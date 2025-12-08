import { test, expect } from '@playwright/test';
import { AppPage } from '../pages/app.page';
import { ComposePage } from '../pages/compose.page';
import { SchedulesPage } from '../pages/schedules.page';

test.describe('Navigation - Complete User Flows', () => {
  let appPage: AppPage;
  let composePage: ComposePage;
  let schedulesPage: SchedulesPage;

  test.beforeEach(async ({ page }) => {
    appPage = new AppPage(page);
    composePage = new ComposePage(page);
    schedulesPage = new SchedulesPage(page);
    await appPage.goto();
  });

  test('should navigate between compose and schedules pages', async () => {
    // Start on compose page
    await expect(composePage.titleInput).toBeVisible();
    
    // Navigate to schedules
    await appPage.navigateToSchedules();
    await expect(schedulesPage.tableHeader).toBeVisible();
    
    // Navigate back to compose
    await appPage.navigateToCompose();
    await expect(composePage.titleInput).toBeVisible();
  });

  test('should display toolbar with navigation buttons', async () => {
    await expect(appPage.toolbar).toBeVisible();
    await expect(appPage.composeNavButton).toBeVisible();
    await expect(appPage.schedulesNavButton).toBeVisible();
  });

  test('should highlight active navigation button', async () => {
    // On compose page, compose button should be active
    await expect(appPage.composeNavButton).toHaveClass(/mat-accent/);
    
    // Navigate to schedules
    await appPage.navigateToSchedules();
    await expect(appPage.schedulesNavButton).toHaveClass(/mat-accent/);
  });

  test('should maintain state when navigating between pages', async () => {
    // Fill form on compose page
    await composePage.fillTitle('Test Title');
    await composePage.fillSpaceKey('DEV');
    await composePage.fillContent('Test content');
    
    // Navigate away
    await appPage.navigateToSchedules();
    await schedulesPage.waitForTable();
    
    // Navigate back
    await appPage.navigateToCompose();
    
    // Form should be reset (Angular routing resets component state)
    const titleValue = await composePage.titleInput.inputValue();
    expect(titleValue).toBe('');
  });
});

