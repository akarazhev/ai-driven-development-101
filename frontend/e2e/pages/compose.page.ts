import { Page, Locator } from '@playwright/test';

export class ComposePage {
  readonly page: Page;
  readonly titleInput: Locator;
  readonly spaceKeyInput: Locator;
  readonly contentTextarea: Locator;
  readonly improveContentButton: Locator;
  readonly createPageButton: Locator;
  readonly publishNowButton: Locator;
  readonly scheduleButton: Locator;
  readonly selectFilesButton: Locator;
  readonly fileInput: Locator;
  readonly uploadButton: Locator;
  readonly suggestionsCard: Locator;
  readonly attachmentsCard: Locator;
  readonly errorMessages: Locator;

  constructor(page: Page) {
    this.page = page;
    // Standard HTML form fields (not Material Design)
    this.titleInput = page.locator('#pageTitle');
    this.spaceKeyInput = page.locator('#spaceKey');
    this.contentTextarea = page.locator('#pageContent');
    
    // Material Design buttons
    this.improveContentButton = page.locator('button:has-text("Improve Content")');
    this.createPageButton = page.locator('button:has-text("Create Page")');
    this.publishNowButton = page.locator('button:has-text("Publish Now")');
    this.scheduleButton = page.locator('button.action-button:has-text("Schedule")');
    this.selectFilesButton = page.locator('button:has-text("Select Files")');
    this.uploadButton = page.locator('button:has-text("Upload Files")');
    
    // Hidden file input
    this.fileInput = page.locator('input[type="file"]');
    
    // Cards
    this.suggestionsCard = page.locator('mat-card:has(mat-card-title:has-text("Content Suggestions"))');
    this.attachmentsCard = page.locator('mat-card:has(mat-card-title:has-text("Attached Files"))');
    
    // Error messages
    this.errorMessages = page.locator('mat-card[style*="background-color"]');
  }

  async goto() {
    await this.page.goto('/');
    await this.titleInput.waitFor({ state: 'visible', timeout: 30000 });
  }

  async fillTitle(title: string) {
    await this.titleInput.fill(title);
  }

  async fillSpaceKey(spaceKey: string) {
    await this.spaceKeyInput.fill(spaceKey);
  }

  async fillContent(content: string) {
    await this.contentTextarea.fill(content);
  }

  async clickImproveContent() {
    await this.improveContentButton.click();
  }

  async clickCreatePage() {
    await this.createPageButton.click();
  }

  async clickPublishNow() {
    await this.publishNowButton.click();
  }

  async clickSchedule() {
    await this.scheduleButton.click();
  }

  async selectFiles(filePaths: string | string[]) {
    await this.selectFilesButton.click();
    const files = Array.isArray(filePaths) ? filePaths : [filePaths];
    await this.fileInput.setInputFiles(files);
  }

  async fillFileDescription(index: number, description: string) {
    const descriptionInput = this.page.locator('.file-description-input').nth(index);
    await descriptionInput.fill(description);
  }

  async clickUpload() {
    await this.uploadButton.click();
  }

  async waitForSuggestions() {
    await this.suggestionsCard.waitFor({ timeout: 10000 });
  }

  async waitForAttachments() {
    await this.attachmentsCard.waitFor({ timeout: 10000 });
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

  async dismissError(index: number = 0) {
    const errorCard = this.errorMessages.nth(index);
    const closeButton = errorCard.locator('button[mat-icon-button]');
    await closeButton.click();
  }

  async waitForButtonEnabled(button: Locator, timeout: number = 5000) {
    await button.waitFor({ state: 'visible', timeout });
    // Wait for button to be enabled (not disabled)
    await this.page.waitForFunction(
      (selector) => {
        const btn = document.querySelector(selector);
        return btn && !btn.hasAttribute('disabled');
      },
      button.locator('button').toString(),
      { timeout }
    );
  }

  async waitForButtonDisabled(button: Locator, timeout: number = 5000) {
    await button.waitFor({ state: 'visible', timeout });
    await button.waitFor({ state: 'attached' });
  }
}

