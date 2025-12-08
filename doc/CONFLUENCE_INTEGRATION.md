# Confluence Integration Guide

This guide explains how to configure the application to integrate with a real Confluence instance.

## Current Configuration

The application is now configured to use the **Confluence API provider** with the following credentials:

- **Confluence URL**: `https://your-domain.atlassian.net/wiki`
- **Username**: `user@example.com`
- **API Token**: Configured (stored in `application.yml` as environment variable)
- **Default Space**: `DS`

## Configuration Files

### Backend Configuration (`application.yml`)

The application is configured to use the Confluence API provider:

```yaml
app:
  confluence-url: https://your-domain.atlassian.net/wiki
  confluence-username: user@example.com
  confluence-api-token: ${CONFLUENCE_API_TOKEN}
  confluence-default-space: DS
  provider: confluence-api
```

### Environment Variables

You can override these values using environment variables:

```bash
# Windows PowerShell
$env:CONFLUENCE_URL="https://your-domain.atlassian.net/wiki"
$env:CONFLUENCE_USERNAME="user@example.com"
$env:CONFLUENCE_API_TOKEN="your-token-here"
$env:CONFLUENCE_DEFAULT_SPACE="DS"
$env:APP_PROVIDER="confluence-api"

# Linux/Mac
export CONFLUENCE_URL="https://your-domain.atlassian.net/wiki"
export CONFLUENCE_USERNAME="user@example.com"
export CONFLUENCE_API_TOKEN="your-token-here"
export CONFLUENCE_DEFAULT_SPACE="DS"
export APP_PROVIDER="confluence-api"
```

## Provider Selection

The application supports two providers:

1. **`confluence-stub`** - Stub provider for development/testing (no real Confluence needed)
2. **`confluence-api`** - Real Confluence API integration

To switch providers, set the `APP_PROVIDER` environment variable or update `application.yml`:

```yaml
app:
  provider: confluence-api  # or confluence-stub
```

## How It Works

### Confluence API Provider

The `ConfluenceApiProvider` implements the `BaseProvider` interface and uses the Confluence REST API to:

1. **Create Pages**: POST to `/rest/api/content`
2. **Upload Attachments**: POST to `/rest/api/content/{pageId}/child/attachment`
3. **Get Page Status**: GET from `/rest/api/content/{pageId}`

### Authentication

The provider uses **Basic Authentication** with:
- Username: Your Confluence username (e.g., `user@example.com`)
- Password: Your Confluence API token

The credentials are Base64-encoded and sent in the `Authorization` header.

### API Endpoints Used

- **Create Page**: `POST {confluence-url}/rest/api/content`
- **Get Page**: `GET {confluence-url}/rest/api/content/{pageId}`
- **Upload Attachment**: `POST {confluence-url}/rest/api/content/{pageId}/child/attachment`

## Testing the Integration

### 1. Start the Backend

```bash
cd backend
./gradlew bootRun
# Windows: .\gradlew.bat bootRun
```

### 2. Verify Configuration

Check the logs for:
```
INFO  - Publishing page 'Test Page' to Confluence space 'DS' (parent: null)
INFO  - Created Confluence page with ID: 12345
INFO  - Successfully published page 'Test Page' with ID: 12345
```

### 3. Test via API

```bash
# Create a page
curl -X POST http://localhost:8080/api/pages \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Page",
    "content": "This is a test page",
    "spaceKey": "DS"
  }'

# Publish the page
curl -X POST http://localhost:8080/api/confluence/publish \
  -H "Content-Type: application/json" \
  -d '{"pageId": 1}'
```

### 4. Check Confluence

Navigate to your Confluence instance and verify the page was created:
```
https://your-domain.atlassian.net/wiki/display/DS/Test+Page
```

## Troubleshooting

### Authentication Errors

**Error**: `401 Unauthorized`

**Solutions**:
1. Verify the API token is correct
2. Check that the username matches your Confluence account
3. Ensure the token hasn't expired
4. Verify the token has the necessary permissions

### Connection Errors

**Error**: `Connection refused` or `Timeout`

**Solutions**:
1. Verify the Confluence URL is correct
2. Check network connectivity
3. Ensure the Confluence instance is accessible
4. Check firewall/proxy settings

### Space Not Found

**Error**: `404 Not Found` when creating a page

**Solutions**:
1. Verify the space key exists (e.g., `DS`)
2. Check that the user has permission to create pages in that space
3. Verify the space key is correct (case-sensitive)

### API Rate Limiting

**Error**: `429 Too Many Requests`

**Solutions**:
1. Reduce the frequency of API calls
2. Implement retry logic with exponential backoff
3. Contact Confluence administrator to increase rate limits

## Security Considerations

### API Token Security

⚠️ **Important**: Never commit API tokens to version control!

1. **Use Environment Variables**: Store tokens in environment variables
2. **Use Secrets Management**: Use a secrets management service in production
3. **Rotate Tokens**: Regularly rotate API tokens
4. **Limit Permissions**: Use tokens with minimal required permissions

### Best Practices

1. **Use Environment Variables**: Store sensitive credentials in environment variables
2. **Use Different Tokens**: Use different tokens for development and production
3. **Monitor Usage**: Monitor API token usage for suspicious activity
4. **Revoke Unused Tokens**: Revoke tokens that are no longer needed

## Switching Back to Stub Provider

If you need to switch back to the stub provider for testing:

```yaml
app:
  provider: confluence-stub
```

Or set the environment variable:
```bash
export APP_PROVIDER=confluence-stub
```

## Additional Resources

- [Confluence REST API Documentation](https://developer.atlassian.com/cloud/confluence/rest/)
- [Confluence API Authentication](https://developer.atlassian.com/cloud/confluence/basic-auth-for-rest-apis/)
- [API Token Management](https://id.atlassian.com/manage-profile/security/api-tokens)

## Support

For issues with the Confluence integration:
1. Check the application logs for detailed error messages
2. Verify your Confluence credentials are correct
3. Test the Confluence API directly using curl or Postman
4. Check the Confluence REST API documentation

