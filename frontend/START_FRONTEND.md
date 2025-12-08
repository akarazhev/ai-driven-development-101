# Starting the Frontend

## Quick Start

### Option 1: Standard Port (4200)
```powershell
cd frontend
npm start
```

### Option 2: Alternative Port (if 4200 is busy)
```powershell
cd frontend
npx ng serve --port 4201
```

## What to Expect

1. **First time**: Takes 30-60 seconds (compilation)
2. **Subsequent runs**: 10-20 seconds
3. **You'll see**: 
   - "Application bundle generation complete"
   - "Local: http://localhost:4200" (or your chosen port)
   - Browser may auto-open

## Verify It's Running

1. Open browser: `http://localhost:4200` (or your port)
2. You should see: "Confluence Publisher" app
3. Check browser console (F12) for any errors

## Troubleshooting

### Port Already in Use
- Use `--port 4201` flag
- Or kill the process: `netstat -ano | findstr :4200` then `taskkill /PID <PID> /F`

### Compilation Errors
- Check browser console (F12)
- Check terminal for error messages
- Try: `npm install` to ensure dependencies are installed

### Can't Connect to Backend
- Verify backend is running: `http://localhost:8080/api/health`
- Check `src/environments/environment.ts` has correct API URL
- Check CORS settings in backend `application.yml`

## Common Commands

```powershell
# Start dev server
npm start

# Start on specific port
npx ng serve --port 4201

# Build for production
npm run build

# Run tests
npm test
```

