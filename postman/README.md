# Postman Collection for Confluence Publisher API

This directory contains the Postman collection for testing the Confluence Publisher API.

## Setup

1. **Import the collection**:
   - Open Postman
   - Click "Import"
   - Select `Confluence Publisher API.postman_collection.json`

2. **Configure environment variables**:
   - Create a new environment in Postman
   - Set `baseUrl` to `http://localhost:8080` (or your backend URL)
   - The collection uses variables like `{{baseUrl}}`, `{{pageId}}`, `{{attachmentId}}`

## Collection Structure

- **Health**: Health check endpoint
- **Attachments**: File upload endpoints
- **Pages**: Page creation and retrieval
- **Schedules**: Scheduling endpoints
- **Confluence Publishing**: Publishing to Confluence
- **AI Features**: AI content improvement

## Usage

1. Start the backend server: `cd backend && ./gradlew bootRun`
2. Open Postman and select the collection
3. Run requests in order:
   - Start with Health Check
   - Upload an attachment (saves `attachmentId`)
   - Create a page (saves `pageId`)
   - Use the saved IDs for subsequent requests

## Test Scripts

Each request includes test scripts that:
- Verify status codes
- Validate response structure
- Save IDs to environment variables for chaining requests

## Environment Variables

The collection automatically sets:
- `attachmentId`: After uploading an attachment
- `pageId`: After creating a page

These are used in subsequent requests automatically.

