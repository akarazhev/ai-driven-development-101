# Required Data and Credentials for Chapter 06

## ✅ Short Answer: **NO CREDENTIALS REQUIRED!**

The project is **fully functional without any external credentials** because it uses a **stub provider** that simulates Confluence.

## 📋 What You Actually Need

### For Development (Required - Already Set Up)
**Nothing!** The project works out of the box:

- ✅ **Database**: SQLite (file-based, no server needed)
- ✅ **Confluence**: Stub provider (simulates Confluence, no real instance needed)
- ✅ **Storage**: Local file system (`storage/attachments`)
- ✅ **API Keys**: None required

### For Production/Real Confluence (Optional - Only if you want real integration)

If you want to connect to a **real Confluence instance** (not required for the course):

#### 1. Confluence URL
- Format: `https://your-domain.atlassian.net`
- Example: `https://mycompany.atlassian.net`
- Where to find: Your Confluence instance URL

#### 2. Confluence API Token
- How to get:
  1. Go to: https://id.atlassian.com/manage-profile/security/api-tokens
  2. Click "Create API token"
  3. Give it a name (e.g., "Confluence Publisher")
  4. Copy the generated token
- Format: A long alphanumeric string
- Example: `ATATT3xFfGF0...` (starts with `ATATT`)

#### 3. Space Key (Optional)
- Default: `DEV` (already configured)
- Examples: `DEV`, `DOCS`, `PROD`, `ENG`
- Where to find: In Confluence, the space key is in the URL or space settings

## 🔧 Configuration Files

### Current Setup (Works Without Credentials)

**Backend Configuration** (`backend/src/main/resources/application.yml`):
```yaml
app:
  provider: confluence-stub  # ← Uses stub (no real Confluence needed)
  database-url: jdbc:sqlite:./data/app.db  # ← SQLite (no server)
  attachment-dir: storage/attachments  # ← Local storage
  confluence-url: https://your-domain.atlassian.net  # ← Not used with stub
  confluence-default-space: DEV  # ← Default space
  scheduler-interval-seconds: 5
```

**Frontend Configuration** (`frontend/src/environments/environment.ts`):
```typescript
export const environment = {
  apiBase: 'http://localhost:8080',  // ← Backend URL
  production: false
};
```

## 🚀 Starting the Application

### Step 1: Start Backend
```powershell
cd backend
./gradlew bootRun
```
- Backend starts on: `http://localhost:8080`
- Database created automatically: `data/app.db`
- No credentials needed!

### Step 2: Start Frontend
```powershell
cd frontend
npm start
```
- Frontend starts on: `http://localhost:4200`
- Connects to backend automatically
- No configuration needed!

### Step 3: Use the App
1. Open: `http://localhost:4200`
2. Create pages, upload attachments, schedule publishing
3. Everything works with the stub provider!

## 🔄 Switching to Real Confluence (Optional)

If you want to use a real Confluence instance:

### 1. Get Your Credentials
- Confluence URL: `https://your-domain.atlassian.net`
- API Token: From Atlassian account settings
- Space Key: Your target space (e.g., `DEV`)

### 2. Set Environment Variable
```powershell
# Windows PowerShell
$env:CONFLUENCE_API_TOKEN="your-token-here"

# Or create .env file (recommended)
```

### 3. Update Configuration
Change in `application.yml`:
```yaml
app:
  provider: confluence  # Change from 'confluence-stub'
  confluence-url: https://your-domain.atlassian.net
  confluence-api-token: ${CONFLUENCE_API_TOKEN:}
```

### 4. Implement Real Provider
- Create `ConfluenceProvider` class
- Implement `BaseProvider` interface
- Use Confluence REST API

**Note**: This is **optional** - the course works perfectly with the stub provider!

## ✅ Verification Checklist

Before starting, verify:

- [x] Java 21+ installed (`java -version`)
- [x] Node.js 18+ installed (`node -v`)
- [x] Gradle available (or use `./gradlew`)
- [x] Ports 8080 and 4200 available
- [x] No credentials needed! ✅

## 📝 Summary

| Item | Required? | What You Need |
|------|-----------|---------------|
| **Confluence URL** | ❌ No | Only if using real Confluence (optional) |
| **Confluence API Token** | ❌ No | Only if using real Confluence (optional) |
| **Database Credentials** | ❌ No | SQLite uses local file (no server) |
| **API Keys** | ❌ No | Stub provider doesn't need any |
| **Login Credentials** | ❌ No | No authentication implemented |

## 🎯 Bottom Line

**You can start implementing Chapter 06 RIGHT NOW without any credentials!**

The stub provider simulates all Confluence operations, so you can:
- ✅ Create pages
- ✅ Upload attachments
- ✅ Schedule publishing
- ✅ Test all features
- ✅ Complete the entire course

All without connecting to a real Confluence instance!

## 📚 Next Steps

1. **Start the application** (see START_HERE.md)
2. **Test the features** (create, schedule, publish)
3. **Follow the milestones** (M1-M4 are mostly done, focus on M4 polishing)
4. **Expand tests** (JUnit, Playwright)
5. **Integrate Material Design** (replace basic UI)

For detailed setup instructions, see: `START_HERE.md`

