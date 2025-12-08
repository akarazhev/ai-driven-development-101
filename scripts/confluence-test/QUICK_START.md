# Quick Start

## Installation

```bash
pip install -r requirements.txt
```

## Run

```bash
python check_confluence_posting.py
```

## What Does the Script Do?

1. ✅ Tests connection to Confluence
2. ✅ Tests authentication (username + API token)
3. ✅ Checks access to specified space
4. ✅ Checks read permissions
5. ✅ **Creates a test page** (verifies write permissions)
6. ✅ Deletes the test page (automatically)

## Result

If all steps pass successfully, you will see:
```
✓ ALL TESTS PASSED: Can post to Confluence!
```

## Troubleshooting

### Connection Error
- Check server availability and correct URL format

### Authentication Error (401)
- Verify username and API token correctness
- Ensure API token is valid and not expired

### Permission Error (403)
- Ensure user has permissions to create pages in the specified space

### Page Creation Failed
- Check user permissions in Confluence
- Verify space exists and is accessible

## Configuration (Recommended)

### Option 1: Using .env File

1. Copy the example file:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` with your credentials:
   ```bash
   CONFLUENCE_URL=https://your-domain.atlassian.net/confluence
   CONFLUENCE_USERNAME=your-username
   CONFLUENCE_API_TOKEN=your-api-token-here
   CONFLUENCE_SPACE_KEY=YOUR_SPACE_KEY
   ```

3. Run the script:
   ```bash
   python check_confluence_posting.py
   ```

### Option 2: Using Environment Variables

```bash
export CONFLUENCE_URL="https://your-domain.atlassian.net/confluence"
export CONFLUENCE_USERNAME="your-username"
export CONFLUENCE_API_TOKEN="your-api-token"
export CONFLUENCE_SPACE_KEY="YOUR_SPACE_KEY"
python check_confluence_posting.py
```

### Option 3: Interactive Input

Just run the script - it will prompt for any missing values:
```bash
python check_confluence_posting.py
```

## Additional Options

You can modify parameters in the code:

```python
# Keep test page (don't delete)
success = tester.run_full_test(space_key="YOUR_SPACE_KEY", cleanup=False)

# Execute only individual steps
tester.test_connection()
tester.test_authentication()
```
