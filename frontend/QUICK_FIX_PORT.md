# Quick Fix: Port 4200 Already in Use

## Problem
```
Port 4200 is already in use.
```

## Solution Options

### Option 1: Use a Different Port (Quickest)
```powershell
npx ng serve --port 4201
```
Then open: `http://localhost:4201`

### Option 2: Kill Process Using Port 4200

1. **Find the process:**
   ```powershell
   netstat -ano | findstr :4200
   ```
   Look for the PID (Process ID) in the last column

2. **Kill the process:**
   ```powershell
   taskkill /PID <PID> /F
   ```
   Replace `<PID>` with the actual process ID

3. **Then start frontend:**
   ```powershell
   npm start
   ```

### Option 3: Check if Frontend is Already Running
Open browser and check: `http://localhost:4200`

If it's already running, you're good to go!

## Recommended: Use Different Port
If you're not sure what's using port 4200, just use a different port:
```powershell
npx ng serve --port 4201
```

Then update your backend CORS settings if needed to allow `http://localhost:4201`

