# 🚀 Starting Chapter 06 Implementation

## ✅ Good News: No Credentials Required!

The project is **already configured to work without any external credentials**:

- ✅ **Uses Stub Provider** - Simulates Confluence API (no real Confluence needed)
- ✅ **SQLite Database** - File-based, no database server needed
- ✅ **No API Keys Required** - Everything works out of the box

## 📋 What You Need

### Required (Already Set Up)
- ✅ Java 21+ installed
- ✅ Node.js 18+ and npm installed
- ✅ Gradle (or use included `gradlew`)
- ✅ Project code (already in place)

### Optional (For Real Confluence Integration Later)
If you want to connect to a **real Confluence instance** (not required for the course):

- Confluence URL: `https://your-domain.atlassian.net`
- Confluence API Token: [Generate from Atlassian Account Settings](https://id.atlassian.com/manage-profile/security/api-tokens)
- Space Key: e.g., `DEV`, `DOCS`, `PROD`

**Note**: You can complete the entire course using the stub provider!

## 🏃 Quick Start

### 1. Start Backend

```powershell
cd backend
./gradlew bootRun
```

Backend will start on: `http://localhost:8080`

**What happens:**
- SQLite database created automatically in `data/app.db`
- Attachments folder created in `storage/attachments`
- Stub provider active (simulates Confluence)
- All endpoints ready

### 2. Start Frontend

```powershell
cd frontend
npm start
# or
npm run start
```

Frontend will start on: `http://localhost:4200`

**What happens:**
- Angular dev server starts
- Connects to backend at `http://localhost:8080`
- UI ready to use

### 3. Test It Works

1. Open browser: `http://localhost:4200`
2. You should see the Compose page
3. Try creating a page:
   - Enter title: "Test Page"
   - Enter content: "This is a test"
   - Enter space key: "DEV"
   - Click "Create page"
4. Check schedules page: `http://localhost:4200/schedules`

## 🎯 Current Implementation Status

### ✅ Already Implemented (Ready to Use)

**M1: Project Scaffold** ✅
- Project structure
- Database schema
- Health endpoint
- Stub provider

**M2: Core Flows** ✅
- Attachment upload
- Page creation
- Scheduling
- Publishing (via stub)
- Basic UI

**M3: AI Features** ✅
- Content improvement
- AI integration in UI

### 📝 What's Next (M4: Polishing)

1. **Expand Tests**
   - More JUnit tests
   - Complete Playwright E2E tests
   - Integration tests

2. **Material Design Integration**
   - Replace basic UI with Material components
   - Apply Material Design patterns

3. **Documentation**
   - Complete API docs
   - Deployment guide

4. **CI/CD**
   - Set up automated testing
   - Deployment pipeline

## 🔧 Configuration (Optional)

### Current Configuration (Works Out of Box)

The app uses these defaults in `backend/src/main/resources/application.yml`:

```yaml
app:
  provider: confluence-stub          # Uses stub (no real Confluence)
  database-url: jdbc:sqlite:./data/app.db
  attachment-dir: storage/attachments
  confluence-url: https://your-domain.atlassian.net  # Not used with stub
  confluence-default-space: DEV
  scheduler-interval-seconds: 5
```

### If You Want Real Confluence (Optional)

1. **Get Confluence API Token:**
   - Go to: https://id.atlassian.com/manage-profile/security/api-tokens
   - Create API token
   - Copy the token

2. **Set Environment Variable:**
   ```powershell
   $env:CONFLUENCE_API_TOKEN="your-token-here"
   ```

3. **Update Configuration:**
   ```yaml
   app:
     provider: confluence  # Change from 'confluence-stub'
     confluence-url: https://your-domain.atlassian.net
     confluence-api-token: ${CONFLUENCE_API_TOKEN:}
   ```

4. **Implement Real Provider:**
   - Create `ConfluenceProvider` class
   - Implement `BaseProvider` interface
   - Use Confluence REST API

**Note**: The course is designed to work with the stub provider. Real Confluence integration is optional.

## 🧪 Testing

### Backend Tests
```powershell
cd backend
./gradlew test
```

### Frontend E2E Tests
```powershell
cd frontend
npm run e2e
```

### Postman Collection
1. Import `postman/Confluence Publisher API.postman_collection.json`
2. Set `baseUrl` to `http://localhost:8080`
3. Run collection

## 📚 Next Steps

1. **Start the application** (backend + frontend)
2. **Test the basic flows** (create page, schedule, publish)
3. **Review the code** to understand the implementation
4. **Follow M4 milestones** to polish and complete

## 🆘 Troubleshooting

### Backend won't start
- Check Java 21 is installed: `java -version`
- Check port 8080 is available
- Check `data/` directory exists (created automatically)

### Frontend won't start
- Run `npm install` in frontend directory
- Check Node.js version: `node -v` (should be 18+)
- Check port 4200 is available

### Can't connect frontend to backend
- Verify backend is running on `http://localhost:8080`
- Check CORS settings in `application.yml`
- Check `frontend/src/environments/environment.ts` has correct API URL

### Database issues
- Delete `data/app.db` to reset database
- Check `data/` directory has write permissions

## ✅ You're Ready!

Everything is set up and ready to go. **No credentials needed** - just start the servers and begin!

For questions or issues, refer to:
- `doc/course/06-project-confluence-publisher-app/README.md` - Full project overview
- `doc/course/06-project-confluence-publisher-app/IMPLEMENTATION_STATUS.md` - Current status
- Individual milestone files (06.1, 06.2, 06.3, 06.4)

