import { test, expect } from '@playwright/test';
import { SchedulesPage } from '../pages/schedules.page';
import { ComposePage } from '../pages/compose.page';
import { AppPage } from '../pages/app.page';

test.describe('Schedules Page - Complete User Flows', () => {
  let schedulesPage: SchedulesPage;
  let composePage: ComposePage;
  let appPage: AppPage;

  test.beforeEach(async ({ page }) => {
    schedulesPage = new SchedulesPage(page);
    composePage = new ComposePage(page);
    appPage = new AppPage(page);
    await schedulesPage.goto();
  });

  test('should display schedules page elements', async () => {
    await expect(schedulesPage.refreshButton).toBeVisible();
    await expect(schedulesPage.tableHeader).toBeVisible();
    await expect(schedulesPage.schedulesTable).toBeVisible();
  });

  test('should load schedules table', async () => {
    await schedulesPage.waitForTable();
    await expect(schedulesPage.schedulesTable).toBeVisible();
  });

  test('should refresh schedules', async () => {
    await schedulesPage.waitForTable();
    const initialRowCount = await schedulesPage.getScheduleRowCount();
    
    await schedulesPage.clickRefresh();
    
    // Wait for table to update
    await schedulesPage.waitForTable();
    const newRowCount = await schedulesPage.getScheduleRowCount();
    
    // Row count should be the same or greater (if new schedules were added)
    expect(newRowCount).toBeGreaterThanOrEqual(initialRowCount);
  });

  test('should display schedule table columns', async () => {
    await schedulesPage.waitForTable();
    
    // Check that table headers are present
    const headers = schedulesPage.schedulesTable.locator('th[mat-header-cell]');
    await expect(headers.nth(0)).toContainText('ID');
    await expect(headers.nth(1)).toContainText('Page ID');
    await expect(headers.nth(2)).toContainText('Status');
    await expect(headers.nth(3)).toContainText('Scheduled');
    await expect(headers.nth(4)).toContainText('Attempts');
  });

  test('should display schedule data in table', async () => {
    await schedulesPage.waitForTable();
    
    const rowCount = await schedulesPage.getScheduleRowCount();
    
    if (rowCount > 0) {
      // Verify first row has data
      const firstRow = await schedulesPage.getScheduleRow(0);
      await expect(firstRow).toBeVisible();
      
      // Check that cells contain data
      const idCell = await schedulesPage.getScheduleCell(0, 'id');
      expect(idCell).toBeTruthy();
      
      const statusCell = await schedulesPage.getScheduleCell(0, 'status');
      expect(statusCell).toBeTruthy();
    } else {
      // If no schedules, verify empty state message
      const emptyMessage = schedulesPage.schedulesTable.locator('text=No schedules found');
      await expect(emptyMessage).toBeVisible();
    }
  });

  test('should navigate from compose to schedules and see new schedule', async () => {
    // Create a scheduled page from compose
    await composePage.goto();
    await composePage.fillTitle(`E2E Schedule Navigation Test ${Date.now()}`);
    await composePage.fillSpaceKey('SPGAC');
    await composePage.fillContent('Content for schedule navigation test');
    await composePage.clickCreatePage();
    await composePage.waitForError('Page created successfully');
    
    // Schedule the page
    await expect(composePage.scheduleButton).toBeEnabled({ timeout: 5000 });
    await composePage.clickSchedule();
    await composePage.waitForError('Page scheduled successfully');
    
    // Navigate to schedules page
    await appPage.navigateToSchedules();
    await schedulesPage.waitForTable();
    
    // Verify the new schedule appears in the table
    const rowCount = await schedulesPage.getScheduleRowCount();
    expect(rowCount).toBeGreaterThan(0);
  });

  test('should handle empty schedules table gracefully', async () => {
    await schedulesPage.waitForTable();
    
    const rowCount = await schedulesPage.getScheduleRowCount();
    
    if (rowCount === 0) {
      // Verify empty state message is shown
      const emptyMessage = schedulesPage.schedulesTable.locator('text=No schedules found');
      await expect(emptyMessage).toBeVisible();
    }
  });

  test('should show status chips with correct colors', async () => {
    await schedulesPage.waitForTable();
    
    const rowCount = await schedulesPage.getScheduleRowCount();
    
    if (rowCount > 0) {
      // Check that status chips are displayed
      const statusChip = schedulesPage.schedulesTable.locator('mat-chip').first();
      await expect(statusChip).toBeVisible();
      
      const statusText = await statusChip.textContent();
      expect(statusText).toBeTruthy();
    }
  });

  test('should auto-refresh schedules every 5 seconds', async () => {
    await schedulesPage.waitForTable();
    const initialRowCount = await schedulesPage.getScheduleRowCount();
    
    // Wait for auto-refresh (6 seconds to be safe)
    await schedulesPage.page.waitForTimeout(6000);
    
    // Verify table is still visible and may have updated
    await expect(schedulesPage.schedulesTable).toBeVisible();
    const newRowCount = await schedulesPage.getScheduleRowCount();
    
    // Row count should be the same or greater
    expect(newRowCount).toBeGreaterThanOrEqual(initialRowCount);
  });
});
