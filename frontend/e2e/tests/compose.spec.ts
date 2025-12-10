import { test, expect } from '@playwright/test';
import { ComposePage } from '../pages/compose.page';
import { AppPage } from '../pages/app.page';
import * as fs from 'fs';
import * as path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

test.describe('Compose Page - Complete User Flows', () => {
  let composePage: ComposePage;
  let appPage: AppPage;
  let testFilePath: string;

  test.beforeEach(async ({ page }) => {
    composePage = new ComposePage(page);
    appPage = new AppPage(page);
    
    // Create a test file for uploads
    const testDir = path.join(__dirname, '../fixtures');
    if (!fs.existsSync(testDir)) {
      fs.mkdirSync(testDir, { recursive: true });
    }
    testFilePath = path.join(testDir, 'test-file.txt');
    fs.writeFileSync(testFilePath, 'Test file content for E2E testing');
    
    await composePage.goto();
  });

  test.afterEach(async () => {
    // Clean up test file
    if (testFilePath && fs.existsSync(testFilePath)) {
      fs.unlinkSync(testFilePath);
    }
  });

  test('should display all compose page elements', async () => {
    // Increase timeout for initial load
    await expect(composePage.titleInput).toBeVisible({ timeout: 10000 });
    await expect(composePage.spaceKeyInput).toBeVisible();
    await expect(composePage.contentTextarea).toBeVisible();
    await expect(composePage.improveContentButton).toBeVisible();
    await expect(composePage.createPageButton).toBeVisible();
    await expect(composePage.publishNowButton).toBeVisible();
    await expect(composePage.scheduleButton).toBeVisible();
    await expect(composePage.selectFilesButton).toBeVisible();
  });

  test('should validate form fields - disable create button when fields are empty', async () => {
    // Initially all buttons should be disabled (except improve content which needs only content)
    await expect(composePage.createPageButton).toBeDisabled();
    
    // Fill only title
    await composePage.fillTitle('Test Title');
    // Blur the input to ensure validation triggers
    await composePage.page.keyboard.press('Tab');
    await expect(composePage.createPageButton).toBeDisabled();
    
    // Fill title and space key
    await composePage.fillSpaceKey('SPGAC');
    await composePage.page.keyboard.press('Tab');
    await expect(composePage.createPageButton).toBeDisabled();
    
    // Fill all required fields
    await composePage.fillContent('Test content');
    // Ensure change detection happens
    await composePage.page.keyboard.press('Tab');
    await expect(composePage.createPageButton).toBeEnabled();
  });

  test('should validate improve content button state', async () => {
    // Initially disabled when content is empty
    await expect(composePage.improveContentButton).toBeDisabled();
    
    // Enabled when content exists
    await composePage.fillContent('Some content to improve');
    // Ensure change detection triggers
    await composePage.page.keyboard.press('Tab');
    await expect(composePage.improveContentButton).toBeEnabled();
  });

  test('should validate publish and schedule buttons require page creation', async () => {
    // Initially disabled (no page created)
    await expect(composePage.publishNowButton).toBeDisabled();
    await expect(composePage.scheduleButton).toBeDisabled();
  });

  test('should create a page successfully', async () => {
    const testTitle = `E2E Test Page ${Date.now()}`;
    const testContent = 'This is test content for E2E testing';
    const testSpaceKey = 'SPGAC';

    await composePage.fillTitle(testTitle);
    await composePage.fillSpaceKey(testSpaceKey);
    await composePage.fillContent(testContent);
    // Ensure all inputs are registered
    await composePage.page.keyboard.press('Tab');
    
    await composePage.clickCreatePage();
    
    // Wait for success message or button to be enabled again
    await composePage.waitForError('Page created successfully');
    
    // Verify publish and schedule buttons are now enabled
    await expect(composePage.publishNowButton).toBeEnabled({ timeout: 10000 });
    await expect(composePage.scheduleButton).toBeEnabled({ timeout: 10000 });
  });

  test('should improve content and show suggestions', async () => {
    const testContent = 'This is a test content that needs improvement for E2E testing purposes';
    
    await composePage.fillContent(testContent);
    // Ensure content is registered
    await composePage.page.keyboard.press('Tab');
    await composePage.clickImproveContent();
    
    // Wait for suggestions to appear
    await composePage.waitForSuggestions();
    
    // Verify suggestions card is visible
    await expect(composePage.suggestionsCard).toBeVisible();
    
    // Verify error message appears
    await composePage.waitForError('Content suggestions generated');
  });

  test('should upload file successfully', async () => {
    await composePage.selectFiles(testFilePath);
    
    // Wait for file to appear in the list
    await expect(composePage.page.locator('text=test-file.txt')).toBeVisible({ timeout: 5000 });
    
    await composePage.clickUpload();
    
    // Wait for success message
    await composePage.waitForError('Files uploaded successfully');
    
    // Verify attachments card appears
    await composePage.waitForAttachments();
    await expect(composePage.attachmentsCard).toBeVisible();
  });

  test('should upload multiple files with descriptions', async () => {
    // Create additional test file
    const testFile2 = path.join(path.dirname(testFilePath), 'test-file-2.txt');
    fs.writeFileSync(testFile2, 'Second test file content');
    
    try {
      await composePage.selectFiles([testFilePath, testFile2]);
      
      // Fill descriptions
      await composePage.fillFileDescription(0, 'First file description');
      await composePage.fillFileDescription(1, 'Second file description');
      
      await composePage.clickUpload();
      
      // Wait for success
      await composePage.waitForError('Files uploaded successfully');
      await composePage.waitForAttachments();
      
      // Verify both files appear
      await expect(composePage.page.locator('text=test-file.txt')).toBeVisible();
      await expect(composePage.page.locator('text=test-file-2.txt')).toBeVisible();
    } finally {
      if (fs.existsSync(testFile2)) {
        fs.unlinkSync(testFile2);
      }
    }
  });

  test('should publish a page after creation', async () => {
    // Create page first
    await composePage.fillTitle(`E2E Publish Test ${Date.now()}`);
    await composePage.fillSpaceKey('SPGAC');
    await composePage.fillContent('Content for publishing');
    await composePage.page.keyboard.press('Tab');
    
    await composePage.clickCreatePage();
    await composePage.waitForError('Page created successfully');
    
    // Wait for publish button to be enabled
    await expect(composePage.publishNowButton).toBeEnabled({ timeout: 10000 });
    
    // Publish the page
    await composePage.clickPublishNow();
    
    // Wait for success message
    await composePage.waitForError('Page published successfully');
  });

  test('should schedule a page after creation', async () => {
    // Create page first
    await composePage.fillTitle(`E2E Schedule Test ${Date.now()}`);
    await composePage.fillSpaceKey('SPGAC');
    await composePage.fillContent('Content for scheduling');
    await composePage.page.keyboard.press('Tab');
    
    await composePage.clickCreatePage();
    await composePage.waitForError('Page created successfully');
    
    // Wait for schedule button to be enabled
    await expect(composePage.scheduleButton).toBeEnabled({ timeout: 10000 });
    
    // Schedule the page
    await composePage.clickSchedule();
    
    // Wait for success message
    await composePage.waitForError('Page scheduled successfully');
  });

  test('should show error when trying to improve empty content', async () => {
    // Try to improve content without filling it
    await composePage.fillContent('');
    await expect(composePage.improveContentButton).toBeDisabled();
  });

  test('should show error when trying to create page with missing fields', async () => {
    // Try to create page with only title
    await composePage.fillTitle('Test Title');
    await expect(composePage.createPageButton).toBeDisabled();
    
    // Try with title and space key but no content
    await composePage.fillSpaceKey('SPGAC');
    await expect(composePage.createPageButton).toBeDisabled();
  });

  test('should show error when trying to publish without creating page', async () => {
    await expect(composePage.publishNowButton).toBeDisabled();
  });

  test('should show error when trying to schedule without creating page', async () => {
    await expect(composePage.scheduleButton).toBeDisabled();
  });

  test('should complete full workflow: create page with attachments, improve content, publish', async () => {
    const testTitle = `E2E Full Workflow ${Date.now()}`;
    const testContent = 'This is comprehensive test content for the full workflow E2E test';
    
    // Step 1: Upload attachment
    await composePage.selectFiles(testFilePath);
    
    // Fill description for file if possible (optional but good for testing)
    const fileDescInputs = await composePage.page.locator('.file-description-input').all();
    if (fileDescInputs.length > 0) {
      await fileDescInputs[0].fill('Test attachment description');
    }

    await composePage.clickUpload();
    await composePage.waitForError('Files uploaded successfully');
    
    // Step 2: Improve content
    await composePage.fillContent(testContent);
    await composePage.page.keyboard.press('Tab');
    await composePage.clickImproveContent();
    await composePage.waitForSuggestions();
    
    // Step 3: Create page
    await composePage.fillTitle(testTitle);
    await composePage.fillSpaceKey('SPGAC');
    await composePage.page.keyboard.press('Tab');
    
    await composePage.clickCreatePage();
    await composePage.waitForError('Page created successfully');
    
    // Step 4: Publish page
    await expect(composePage.publishNowButton).toBeEnabled({ timeout: 10000 });
    await composePage.clickPublishNow();
    await composePage.waitForError('Page published successfully');
  });
});
