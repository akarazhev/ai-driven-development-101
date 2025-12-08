import { Page, Locator } from '@playwright/test';

export class SchedulesPage {
  readonly page: Page;
  readonly refreshButton: Locator;
  readonly schedulesTable: Locator;
  readonly tableHeader: Locator;
  readonly errorMessages: Locator;

  constructor(page: Page) {
    this.page = page;
    this.refreshButton = page.locator('button:has-text("Refresh")');
    this.schedulesTable = page.locator('table[mat-table]');
    this.tableHeader = page.locator('mat-card-title:has-text("Publication Schedules")');
    this.errorMessages = page.locator('mat-card[style*="background-color"]');
  }

  async goto() {
    await this.page.goto('/schedules');
    await this.page.waitForLoadState('networkidle');
  }

  async clickRefresh() {
    await this.refreshButton.click();
  }

  async waitForTable() {
    await this.schedulesTable.waitFor({ timeout: 10000 });
  }

  async getScheduleRows() {
    return this.schedulesTable.locator('tbody tr[mat-row]');
  }

  async getScheduleRowCount(): Promise<number> {
    const rows = await this.getScheduleRows();
    return rows.count();
  }

  async getScheduleRow(index: number) {
    const rows = await this.getScheduleRows();
    return rows.nth(index);
  }

  async getScheduleCell(rowIndex: number, columnName: string): Promise<string | null> {
    const row = await this.getScheduleRow(rowIndex);
    // Material table cells
    const cell = row.locator(`td[mat-cell]:nth-child(${this.getColumnIndex(columnName)})`);
    return cell.textContent();
  }

  private getColumnIndex(columnName: string): number {
    const columns: { [key: string]: number } = {
      'id': 1,
      'pageId': 2,
      'status': 3,
      'scheduledAt': 4,
      'attemptCount': 5
    };
    return columns[columnName] || 1;
  }

  async waitForError(message?: string) {
    if (message) {
      await this.page.waitForSelector(`text=${message}`, { timeout: 5000 });
    } else {
      await this.errorMessages.first().waitFor({ timeout: 5000 });
    }
  }
}

