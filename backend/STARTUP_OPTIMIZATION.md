# Backend Startup Optimization

## Current Status
- Normal startup time: ~5 seconds
- If taking longer, see optimizations below

## Optimizations Applied

### 1. JPA/Hibernate Optimizations
- Disabled `open-in-view` (removes warning, improves performance)
- Added batch processing settings
- Optimized insert/update ordering

### 2. Logging Optimizations
- Reduced startup logging verbosity
- Only show WARN and above for Spring Boot during startup

### 3. DevTools Configuration
- Optimized file watching exclusions
- Faster restart detection

## Additional Optimizations (If Still Slow)

### Option 1: Enable Lazy Initialization (Fastest Startup)
Add to `application.yml`:
```yaml
spring:
  main:
    lazy-initialization: true
```
**Note**: This delays bean creation until needed, but may hide startup errors.

### Option 2: Disable DevTools (For Production-like Performance)
Remove or comment out in `build.gradle.kts`:
```kotlin
// developmentOnly("org.springframework.boot:spring-boot-devtools")
```

### Option 3: Optimize Component Scanning
Add to main application class:
```java
@SpringBootApplication(scanBasePackages = "com.confluence.publisher")
```

### Option 4: Use Profile-Specific Configuration
Create `application-dev.yml` for development optimizations.

## Troubleshooting Slow Startup

### Check What's Taking Time:
1. **First run**: Dependencies download (one-time, can take 1-2 minutes)
2. **Database initialization**: First run creates schema (~1-2 seconds)
3. **Component scanning**: Spring scans for beans (~1-2 seconds)
4. **DevTools**: File watching setup (~1 second)

### If Startup is >10 seconds:
1. Check network connection (dependency downloads)
2. Check disk I/O (database/file operations)
3. Check antivirus (may scan files)
4. Check if port 8080 is already in use

### Quick Test:
```powershell
# Clean build and measure time
.\gradlew.bat clean bootRun --info
```

## Expected Startup Times

- **First run**: 30-60 seconds (dependency download)
- **Subsequent runs**: 3-8 seconds (normal)
- **With lazy init**: 1-3 seconds (faster but delayed errors)

## Performance Tips

1. **Keep DevTools enabled** for development (fast restarts)
2. **Disable DevTools** for production builds
3. **Use lazy initialization** only if startup is critical
4. **Monitor logs** to identify slow components

