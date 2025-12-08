# Troubleshooting Guide

## Common Issues and Solutions

### Issue: "data directory does not exist"

**Error:**
```
Unable to open JDBC Connection for DDL execution [path to './data/app.db': 'C:\...\backend\.\data' does not exist]
```

**Solution:**
The directories are now created automatically, but if you still see this error:

1. **Manual fix (one-time):**
   ```powershell
   cd backend
   New-Item -ItemType Directory -Path "data" -Force
   New-Item -ItemType Directory -Path "storage\attachments" -Force
   ```

2. **Automatic fix:**
   The code now creates these directories automatically on startup. If you still have issues:
   - Delete the `build` directory: `Remove-Item -Recurse -Force build`
   - Rebuild: `.\gradlew.bat clean build`
   - Run again: `.\gradlew.bat bootRun`

### Issue: Port 8080 already in use

**Error:**
```
Web server failed to start. Port 8080 was already in use.
```

**Solution:**
1. Find what's using the port:
   ```powershell
   netstat -ano | findstr :8080
   ```
2. Kill the process (replace PID with actual process ID):
   ```powershell
   taskkill /PID <PID> /F
   ```
3. Or change the port in `application.yml`:
   ```yaml
   server:
     port: 8081
   ```

### Issue: Java version problems

**Error:**
```
Unsupported class file major version XX
```

**Solution:**
- Ensure Java 21+ is installed: `java -version`
- Set JAVA_HOME if needed:
  ```powershell
  $env:JAVA_HOME="C:\Program Files\Java\jdk-21"
  ```

### Issue: Gradle wrapper not found

**Error:**
```
'gradlew' is not recognized
```

**Solution:**
- On Windows, use: `.\gradlew.bat bootRun`
- Make sure you're in the `backend` directory
- The `gradlew.bat` file should exist in the backend folder

### Issue: Frontend can't connect to backend

**Error:**
```
Failed to fetch
CORS error
```

**Solution:**
1. Verify backend is running: `http://localhost:8080/api/health`
2. Check CORS settings in `application.yml`:
   ```yaml
   app:
     cors-origins:
       - http://localhost:4200
   ```
3. Check frontend environment: `frontend/src/environments/environment.ts`
   ```typescript
   apiBase: 'http://localhost:8080'
   ```

### Issue: Database locked

**Error:**
```
database is locked
```

**Solution:**
- Close any other applications using the database
- Delete `data/app.db` and restart (database will be recreated)
- Make sure only one instance of the backend is running

### Issue: Attachment upload fails

**Error:**
```
Failed to save attachment
```

**Solution:**
1. Check `storage/attachments` directory exists
2. Verify write permissions
3. Check file size limits in `application.yml`:
   ```yaml
   spring:
     servlet:
       multipart:
         max-file-size: 50MB
   ```

## Quick Diagnostic Commands

### Check if backend is running:
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/health"
```

### Check Java version:
```powershell
java -version
```

### Check if directories exist:
```powershell
Test-Path "data"
Test-Path "storage\attachments"
```

### Clean and rebuild:
```powershell
.\gradlew.bat clean build
```

### View logs:
Check the console output when running `.\gradlew.bat bootRun`

## Still Having Issues?

1. **Check the logs** - Look for error messages in the console
2. **Verify prerequisites**:
   - Java 21+ installed
   - Node.js 18+ installed (for frontend)
   - Ports 8080 and 4200 available
3. **Try clean build**:
   ```powershell
   .\gradlew.bat clean
   .\gradlew.bat bootRun
   ```
4. **Check file permissions** - Ensure you have write access to:
   - `data/` directory
   - `storage/attachments/` directory
   - `build/` directory

