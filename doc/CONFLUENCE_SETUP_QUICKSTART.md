# Confluence Integration - Quick Start

## ✅ Current Setup

The application is **already configured** to use the real Confluence API with:

- **URL**: `https://pmc-stage.specific-group.eu/confluence`
- **Username**: `spg.academy`
- **Space**: `SPGAC`
- **Provider**: `confluence-api` (real integration)

## 🚀 Quick Start

### 1. Start the Backend

```bash
cd backend
./gradlew bootRun
# Windows: .\gradlew.bat bootRun
```

The backend will automatically use the Confluence API provider.

### 2. Start the Frontend

```bash
cd frontend
npm start
```

### 3. Test the Integration

1. Open `http://localhost:4200`
2. Create a page with:
   - Title: "Test Page"
   - Content: "This is a test"
   - Space Key: `SPGAC`
3. Click "Create Page"
4. Click "Publish Now"
5. Check your Confluence instance to see the published page

## 🔧 Configuration

### Current Settings (in `application.yml`)

```yaml
app:
  confluence-url: https://pmc-stage.specific-group.eu/confluence
  confluence-username: spg.academy
  confluence-default-space: SPGAC
  provider: confluence-api
```

### Override with Environment Variables

```bash
# Windows PowerShell
$env:CONFLUENCE_URL="https://pmc-stage.specific-group.eu/confluence"
$env:CONFLUENCE_USERNAME="spg.academy"
$env:CONFLUENCE_API_TOKEN="your-token"
$env:CONFLUENCE_DEFAULT_SPACE="SPGAC"
$env:APP_PROVIDER="confluence-api"
```

## 🔄 Switch to Stub Provider (for testing)

If you want to test without real Confluence:

```yaml
app:
  provider: confluence-stub
```

Or:
```bash
$env:APP_PROVIDER="confluence-stub"
```

## ✅ Verification

Check the backend logs for successful API calls:

```
INFO  - Publishing page 'Test Page' to Confluence space 'SPGAC'
INFO  - Created Confluence page with ID: 12345
INFO  - Successfully published page 'Test Page' with ID: 12345
```

## 📚 More Information

See [CONFLUENCE_INTEGRATION.md](CONFLUENCE_INTEGRATION.md) for detailed documentation.

