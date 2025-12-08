# Frontend Troubleshooting Guide

## Quick Start

### Start Frontend:
```powershell
cd frontend
npm start
```

Or using npx:
```powershell
cd frontend
npx ng serve
```

## Common Issues

### Issue: "ng is not recognized"
**Solution:**
- Use `npx ng` instead of just `ng`
- Or use `npm start` which uses the local Angular CLI

### Issue: Port 4200 already in use
**Error:**
```
Port 4200 is already in use
```

**Solution:**
1. Find what's using the port:
   ```powershell
   netstat -ano | findstr :4200
   ```
2. Kill the process (replace PID):
   ```powershell
   taskkill /PID <PID> /F
   ```
3. Or use a different port:
   ```powershell
   npx ng serve --port 4201
   ```

### Issue: Module not found errors
**Error:**
```
Cannot find module '@angular/...'
```

**Solution:**
```powershell
cd frontend
npm install
```

### Issue: TypeScript compilation errors
**Error:**
```
error TS2307: Cannot find module '...'
```

**Solution:**
1. Check if dependencies are installed: `npm install`
2. Check `tsconfig.json` configuration
3. Restart the dev server

### Issue: Material theme not found
**Error:**
```
Cannot find module '@angular/material/prebuilt-themes/...'
```

**Solution:**
1. Verify Angular Material is installed:
   ```powershell
   npm list @angular/material
   ```
2. If not installed:
   ```powershell
   npm install @angular/material @angular/cdk
   ```

### Issue: Build fails
**Error:**
```
Build failed
```

**Solution:**
1. Clean and rebuild:
   ```powershell
   Remove-Item -Recurse -Force node_modules
   Remove-Item -Recurse -Force .angular
   npm install
   npm start
   ```

## Verification Steps

### Check if frontend is running:
1. Open browser: `http://localhost:4200`
2. You should see the Confluence Publisher app
3. Check browser console for errors (F12)

### Check Angular CLI:
```powershell
npx ng version
```

### Check dependencies:
```powershell
npm list --depth=0
```

## Expected Behavior

When `npm start` runs successfully:
1. Angular CLI compiles the application
2. Dev server starts on port 4200
3. Browser automatically opens (if configured)
4. You see: "Application bundle generation complete"
5. Hot reload is enabled (changes auto-refresh)

## Performance Tips

1. **First run is slower** - Initial compilation takes time
2. **Subsequent runs are faster** - Incremental compilation
3. **Use `--port` flag** if 4200 is busy
4. **Use `--open=false`** to prevent auto-opening browser

## Quick Commands

```powershell
# Start dev server
npm start

# Start on different port
npx ng serve --port 4201

# Build for production
npm run build

# Run tests
npm test

# Run E2E tests
npm run e2e
```

