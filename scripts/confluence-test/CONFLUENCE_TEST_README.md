# Confluence API Posting Test Script

Python script to test the ability to post content to Confluence via REST API.

## Test Steps Description

The script performs the following steps:

### Step 1: Connection Test
- Checks Confluence server availability
- Retrieves server version information
- Uses endpoint: `/rest/api/serverInfo`

### Step 2: Authentication Test
- Validates credentials (username + API token)
- Retrieves current user information
- Uses endpoint: `/rest/api/user/current`

### Step 3: Space Information Retrieval
- Checks if the specified space exists (space key)
- Retrieves space metadata
- Uses endpoint: `/rest/api/space?keys={SPACE_KEY}`

### Step 4: Permission Check
- Checks ability to read content from the space
- Uses endpoint: `/rest/api/content?spaceKey={SPACE_KEY}`

### Step 5: Test Page Creation
- Creates a test page in the specified space
- Verifies write permissions
- Uses endpoint: `POST /rest/api/content`
- **Important**: The page will be created with title "API Test Page - Please Delete"

### Step 6: Test Page Deletion (Optional)
- Deletes the created test page
- Uses endpoint: `DELETE /rest/api/content/{pageId}`
- Executes automatically if `cleanup=True`

## Requirements

- Python 3.6+
- `requests` library

## Installation

```bash
pip install -r requirements.txt
```

This will install:
- `requests` - for HTTP requests
- `python-dotenv` - for loading `.env` files

## Usage

### Basic Usage

```bash
python check_confluence_posting.py
```

### Programmatic Usage

```python
from check_confluence_posting import ConfluenceTester
import os

# Get credentials from environment or user input
base_url = os.getenv("CONFLUENCE_URL", "https://your-domain.atlassian.net/confluence")
username = os.getenv("CONFLUENCE_USERNAME", "your-username")
api_token = os.getenv("CONFLUENCE_API_TOKEN", "your-api-token")
space_key = os.getenv("CONFLUENCE_SPACE_KEY", "YOUR_SPACE_KEY")

# Create tester instance
tester = ConfluenceTester(base_url, username, api_token)

# Run full test
success = tester.run_full_test(space_key=space_key, cleanup=True)

# Or execute steps individually
tester.test_connection()
tester.test_authentication()
tester.get_space_info(space_key)
tester.check_space_permissions(space_key)
page_id = tester.create_test_page(space_key)
if page_id:
    tester.delete_test_page(page_id)
```

## Configuration

The script supports multiple ways to provide credentials (in order of priority):

1. **`.env` file** (recommended for local development)
2. **Environment variables** (recommended for CI/CD)
3. **Interactive prompts** (fallback)

### Using .env File (Recommended)

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

⚠️ **Important**: The `.env` file is already in `.gitignore` and will not be committed to the repository.

## Troubleshooting

### Issue: Connection failed
- **Cause**: Server unavailable or incorrect URL
- **Solution**: Check server availability and base URL correctness

### Issue: Authentication failed (401)
- **Cause**: Invalid credentials
- **Solution**: Verify username and API token

### Issue: Space not found (404)
- **Cause**: Incorrect space key
- **Solution**: Verify space key correctness

### Issue: Permission denied (403)
- **Cause**: Insufficient permissions for the operation
- **Solution**: Ensure user has permissions to create content in the space

### Issue: Failed to create test page
- **Cause**: No write permissions or data format issues
- **Solution**: Check user permissions and request format

## API Endpoints Used

1. `GET /rest/api/serverInfo` - server information
2. `GET /rest/api/user/current` - current user
3. `GET /rest/api/space?keys={key}` - space information
4. `GET /rest/api/content?spaceKey={key}` - space content
5. `POST /rest/api/content` - page creation
6. `DELETE /rest/api/content/{id}` - page deletion

## Security

⚠️ **Important**: 
- Do not commit API tokens to repository
- Use environment variables for sensitive data storage
- Regularly update API tokens

## Environment Variables (Alternative)

You can also set environment variables before running:

```bash
export CONFLUENCE_URL="https://your-domain.atlassian.net/confluence"
export CONFLUENCE_USERNAME="your-username"
export CONFLUENCE_API_TOKEN="your-api-token"
export CONFLUENCE_SPACE_KEY="YOUR_SPACE_KEY"
python check_confluence_posting.py
```

Or set only some values - the script will prompt for missing ones:

```bash
export CONFLUENCE_API_TOKEN="your_token_here"
python check_confluence_posting.py
```

## Credential Priority

The script loads credentials in this order:
1. `.env` file (if exists in script directory)
2. Environment variables
3. Interactive prompts (for missing values)

## Additional Resources

- [Confluence REST API Documentation](https://developer.atlassian.com/server/confluence/confluence-server-rest-api/)
- [Confluence REST API Examples](https://developer.atlassian.com/server/confluence/confluence-rest-api-examples/)
- [Authentication with Personal Access Tokens](https://confluence.atlassian.com/enterprise/using-personal-access-tokens-1026032365.html)
