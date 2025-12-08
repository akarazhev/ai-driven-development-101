import { Page, Locator } from '@playwright/test';

export class AppPage {
  readonly page: Page;
  readonly composeNavButton: Locator;
  readonly schedulesNavButton: Locator;
  readonly toolbar: Locator;
  readonly errorMessages: Locator;

  constructor(page: Page) {
    this.page = page;
    this.toolbar = page.locator('mat-toolbar.app-toolbar');
    this.composeNavButton = page.locator('button:has-text("Compose")');
    this.schedulesNavButton = page.locator('button:has-text("Schedules")');
    this.errorMessages = page.locator('mat-card[style*="background-color"]');
  }

  async goto() {
    await this.page.goto('/');
    await this.page.waitForLoadState('networkidle');
  }

  async navigateToCompose() {
    await this.composeNavButton.click();
    await this.page.waitForURL('/');
  }

  async navigateToSchedules() {
    await this.schedulesNavButton.click();
    await this.page.waitForURL('/schedules');
  }

  async waitForError(message?: string) {
    if (message) {
      await this.page.waitForSelector(`text=${message}`, { timeout: 5000 });
    } else {
      await this.errorMessages.first().waitFor({ timeout: 5000 });
    }
  }

  async getErrorMessages(): Promise<string[]> {
    const errors = await this.errorMessages.all();
    return Promise.all(errors.map(async (error) => await error.textContent() || ''));
  }
}

