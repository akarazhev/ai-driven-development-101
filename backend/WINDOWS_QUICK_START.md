# Windows Quick Start Guide

## ✅ Problem Solved!

On Windows, you need to use `gradlew.bat` instead of `./gradlew`.

## 🚀 Running the Backend

### Correct Command (Windows):
```powershell
.\gradlew.bat bootRun
```

### Alternative (also works):
```powershell
gradlew.bat bootRun
```

### ❌ Don't Use (Unix/Linux only):
```powershell
./gradlew bootRun  # This won't work on Windows!
```

## 📋 Complete Startup Steps

### 1. Start Backend (Terminal 1)
```powershell
cd backend
.\gradlew.bat bootRun
```

Wait for: `Started ConfluencePublisherApplication in X.XXX seconds`

Backend will be available at: `http://localhost:8080`

### 2. Start Frontend (Terminal 2)
```powershell
cd frontend
npm start
```

Wait for: `Application bundle generation complete`

Frontend will be available at: `http://localhost:4200`

### 3. Test It Works
1. Open browser: `http://localhost:4200`
2. You should see the Compose page
3. Try creating a test page

## 🔧 Other Gradle Commands (Windows)

```powershell
# Run tests
.\gradlew.bat test

# Build the project
.\gradlew.bat build

# Clean build
.\gradlew.bat clean build

# Check Gradle version
.\gradlew.bat --version
```

## 🆘 Troubleshooting

### Issue: "JAVA_HOME is not set"
**Solution:**
1. Install Java 21+ if not installed
2. Set JAVA_HOME environment variable:
   ```powershell
   $env:JAVA_HOME="C:\Program Files\Java\jdk-21"
   ```
3. Or add Java to PATH

### Issue: Port 8080 already in use
**Solution:**
- Stop other applications using port 8080
- Or change port in `application.yml`:
  ```yaml
  server:
    port: 8081  # Change to different port
  ```

### Issue: "gradlew.bat not found"
**Solution:**
- Make sure you're in the `backend` directory
- The `gradlew.bat` file should be in the same directory

## ✅ Verification

After running `.\gradlew.bat bootRun`, you should see:
- Gradle downloading dependencies (first time only)
- Spring Boot starting
- Database initializing
- Server starting on port 8080
- Message: `Started ConfluencePublisherApplication`

## 📝 Notes

- First run may take longer (downloading dependencies)
- Keep the terminal open while backend is running
- Press `Ctrl+C` to stop the backend
- Use `.\gradlew.bat` for all Gradle commands on Windows

