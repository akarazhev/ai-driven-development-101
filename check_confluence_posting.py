#!/usr/bin/env python3
"""
Confluence API Connection and Posting Test Script
Tests the ability to connect, authenticate, and post content to Confluence.
"""

import requests
import json
import base64
from typing import Optional, Dict, Any
from urllib.parse import urljoin


class ConfluenceTester:
    """Test class for Confluence API operations."""
    
    def __init__(self, base_url: str, username: str, api_token: str):
        """
        Initialize Confluence tester.
        
        Args:
            base_url: Base URL of Confluence instance (without trailing slash)
            username: Username for authentication
            api_token: Personal access token or API token
        """
        self.base_url = base_url.rstrip('/')
        self.api_base = f"{self.base_url}/rest/api"
        self.username = username
        self.api_token = api_token
        self.session = requests.Session()
        self._setup_auth()
    
    def _setup_auth(self):
        """Setup basic authentication."""
        # Try different authentication methods
        # Method 1: Basic Auth with username:token
        from requests.auth import HTTPBasicAuth
        self.session.auth = HTTPBasicAuth(self.username, self.api_token)
        self.session.headers.update({
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        })
    
    def _try_bearer_auth(self):
        """Try Bearer token authentication as alternative."""
        self.session.auth = None
        self.session.headers.update({
            'Authorization': f'Bearer {self.api_token}',
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        })
    
    def test_connection(self) -> bool:
        """
        Step 1: Test basic connection to Confluence.
        
        Returns:
            True if connection successful, False otherwise
        """
        print("=" * 60)
        print("Step 1: Testing connection to Confluence...")
        print("=" * 60)
        
        # Try Basic Auth first with /user/current endpoint (more reliable)
        try:
            # Try /user/current first as it's more universally available
            response = self.session.get(f"{self.api_base}/user/current")
            response.raise_for_status()
            
            user_info = response.json()
            print(f"✓ Connection successful with Basic Auth!")
            print(f"  Authenticated as: {user_info.get('displayName', user_info.get('username', 'Unknown'))}")
            
            # Try to get server info if available
            try:
                server_response = self.session.get(f"{self.api_base}/serverInfo")
                if server_response.status_code == 200:
                    server_info = server_response.json()
                    print(f"  Server: {server_info.get('serverTitle', 'Unknown')}")
                    print(f"  Version: {server_info.get('version', 'Unknown')}")
            except:
                pass  # Server info endpoint may not be available
            
            return True
            
        except requests.exceptions.HTTPError as e:
            if e.response.status_code == 401:
                print("  Basic Auth failed, trying Bearer token...")
                # Try Bearer token
                self._try_bearer_auth()
                try:
                    response = self.session.get(f"{self.api_base}/user/current")
                    response.raise_for_status()
                    
                    user_info = response.json()
                    print(f"✓ Connection successful with Bearer token!")
                    print(f"  Authenticated as: {user_info.get('displayName', user_info.get('username', 'Unknown'))}")
                    
                    # Try to get server info if available
                    try:
                        server_response = self.session.get(f"{self.api_base}/serverInfo")
                        if server_response.status_code == 200:
                            server_info = server_response.json()
                            print(f"  Server: {server_info.get('serverTitle', 'Unknown')}")
                            print(f"  Version: {server_info.get('version', 'Unknown')}")
                    except:
                        pass
                    
                    return True
                except requests.exceptions.RequestException as e2:
                    print(f"✗ Connection failed with both methods: {e2}")
                    if hasattr(e2, 'response') and e2.response is not None:
                        print(f"  Status Code: {e2.response.status_code}")
                        print(f"  Response: {e2.response.text[:500]}")
                    return False
            else:
                print(f"✗ Connection failed: {e}")
                if hasattr(e, 'response') and e.response is not None:
                    print(f"  Status Code: {e.response.status_code}")
                    print(f"  Response: {e.response.text[:200]}")
                return False
        except requests.exceptions.RequestException as e:
            print(f"✗ Connection failed: {e}")
            if hasattr(e, 'response') and e.response is not None:
                print(f"  Status Code: {e.response.status_code}")
                print(f"  Response: {e.response.text[:200]}")
            return False
    
    def test_authentication(self) -> bool:
        """
        Step 2: Test authentication.
        
        Returns:
            True if authentication successful, False otherwise
        """
        print("\n" + "=" * 60)
        print("Step 2: Testing authentication...")
        print("=" * 60)
        
        try:
            # Try to get current user info
            response = self.session.get(f"{self.api_base}/user/current")
            response.raise_for_status()
            
            user_info = response.json()
            print(f"✓ Authentication successful!")
            print(f"  Username: {user_info.get('username', 'Unknown')}")
            print(f"  Display Name: {user_info.get('displayName', 'Unknown')}")
            print(f"  Email: {user_info.get('email', 'Unknown')}")
            return True
            
        except requests.exceptions.RequestException as e:
            print(f"✗ Authentication failed: {e}")
            if hasattr(e, 'response') and e.response is not None:
                print(f"  Status Code: {e.response.status_code}")
                print(f"  Response: {e.response.text[:200]}")
            return False
    
    def get_space_info(self, space_key: str) -> Optional[Dict[str, Any]]:
        """
        Step 3: Get space information.
        
        Args:
            space_key: Space key (e.g., 'SPGAC')
            
        Returns:
            Space information dict or None if failed
        """
        print("\n" + "=" * 60)
        print(f"Step 3: Getting space information for '{space_key}'...")
        print("=" * 60)
        
        try:
            response = self.session.get(
                f"{self.api_base}/space",
                params={'keys': space_key}
            )
            response.raise_for_status()
            
            result = response.json()
            if 'results' in result and len(result['results']) > 0:
                space = result['results'][0]
                print(f"✓ Space found!")
                print(f"  Key: {space.get('key', 'Unknown')}")
                print(f"  Name: {space.get('name', 'Unknown')}")
                print(f"  Type: {space.get('type', 'Unknown')}")
                return space
            else:
                print(f"✗ Space '{space_key}' not found")
                return None
                
        except requests.exceptions.RequestException as e:
            print(f"✗ Failed to get space info: {e}")
            if hasattr(e, 'response') and e.response is not None:
                print(f"  Status Code: {e.response.status_code}")
                print(f"  Response: {e.response.text[:200]}")
            return None
    
    def check_space_permissions(self, space_key: str) -> bool:
        """
        Step 4: Check if user has permission to create content in space.
        
        Args:
            space_key: Space key to check
            
        Returns:
            True if user has permissions, False otherwise
        """
        print("\n" + "=" * 60)
        print(f"Step 4: Checking permissions for space '{space_key}'...")
        print("=" * 60)
        
        try:
            # Try to get space content to check read permission
            response = self.session.get(
                f"{self.api_base}/content",
                params={
                    'spaceKey': space_key,
                    'limit': 1
                }
            )
            response.raise_for_status()
            
            print(f"✓ Can read content from space '{space_key}'")
            
            # Note: Write permission can only be truly tested by attempting to create content
            print(f"  (Write permission will be tested in next step)")
            return True
            
        except requests.exceptions.RequestException as e:
            print(f"✗ Permission check failed: {e}")
            if hasattr(e, 'response') and e.response is not None:
                print(f"  Status Code: {e.response.status_code}")
                if e.response.status_code == 403:
                    print(f"  Access denied - insufficient permissions")
                print(f"  Response: {e.response.text[:200]}")
            return False
    
    def create_test_page(self, space_key: str, parent_id: Optional[str] = None) -> Optional[str]:
        """
        Step 5: Create a test page to verify write permissions.
        
        Args:
            space_key: Space key where to create the page
            parent_id: Optional parent page ID
            
        Returns:
            Created page ID or None if failed
        """
        print("\n" + "=" * 60)
        print(f"Step 5: Creating test page in space '{space_key}'...")
        print("=" * 60)
        
        page_data = {
            "type": "page",
            "title": "API Test Page - Please Delete",
            "space": {
                "key": space_key
            },
            "body": {
                "storage": {
                    "value": "<p>This is a test page created by the Confluence API test script.</p><p>You can safely delete this page.</p>",
                    "representation": "storage"
                }
            }
        }
        
        if parent_id:
            page_data["ancestors"] = [{"id": parent_id}]
        
        try:
            response = self.session.post(
                f"{self.api_base}/content",
                json=page_data
            )
            response.raise_for_status()
            
            created_page = response.json()
            page_id = created_page.get('id')
            print(f"✓ Test page created successfully!")
            print(f"  Page ID: {page_id}")
            print(f"  Title: {created_page.get('title', 'Unknown')}")
            print(f"  URL: {self.base_url}{created_page.get('_links', {}).get('webui', '')}")
            return page_id
            
        except requests.exceptions.RequestException as e:
            print(f"✗ Failed to create test page: {e}")
            if hasattr(e, 'response') and e.response is not None:
                print(f"  Status Code: {e.response.status_code}")
                print(f"  Response: {e.response.text[:500]}")
            return None
    
    def delete_test_page(self, page_id: str) -> bool:
        """
        Step 6: Delete the test page.
        
        Args:
            page_id: ID of the page to delete
            
        Returns:
            True if deletion successful, False otherwise
        """
        print("\n" + "=" * 60)
        print(f"Step 6: Deleting test page '{page_id}'...")
        print("=" * 60)
        
        try:
            response = self.session.delete(f"{self.api_base}/content/{page_id}")
            response.raise_for_status()
            
            print(f"✓ Test page deleted successfully!")
            return True
            
        except requests.exceptions.RequestException as e:
            print(f"✗ Failed to delete test page: {e}")
            if hasattr(e, 'response') and e.response is not None:
                print(f"  Status Code: {e.response.status_code}")
                print(f"  Response: {e.response.text[:200]}")
            return False
    
    def run_full_test(self, space_key: str, cleanup: bool = True) -> bool:
        """
        Run complete test suite.
        
        Args:
            space_key: Space key to test
            cleanup: Whether to delete test page after creation
            
        Returns:
            True if all tests passed, False otherwise
        """
        print("\n" + "=" * 60)
        print("CONFLUENCE API POSTING TEST")
        print("=" * 60)
        print(f"Base URL: {self.base_url}")
        print(f"Space Key: {space_key}")
        print(f"Username: {self.username}")
        print("=" * 60)
        
        # Step 1: Test connection
        if not self.test_connection():
            return False
        
        # Step 2: Test authentication
        if not self.test_authentication():
            return False
        
        # Step 3: Get space info
        space_info = self.get_space_info(space_key)
        if not space_info:
            return False
        
        # Step 4: Check permissions
        if not self.check_space_permissions(space_key):
            return False
        
        # Step 5: Create test page
        page_id = self.create_test_page(space_key)
        if not page_id:
            print("\n" + "=" * 60)
            print("✗ TEST FAILED: Cannot create pages in Confluence")
            print("=" * 60)
            return False
        
        # Step 6: Cleanup (delete test page)
        if cleanup:
            self.delete_test_page(page_id)
        else:
            print("\n" + "=" * 60)
            print("⚠ Test page left in Confluence (cleanup disabled)")
            print(f"  Page ID: {page_id}")
            print("  Please delete it manually")
            print("=" * 60)
        
        print("\n" + "=" * 60)
        print("✓ ALL TESTS PASSED: Can post to Confluence!")
        print("=" * 60)
        return True


def main():
    """Main function to run the test."""
    import os
    
    # Configuration - can be overridden by environment variables
    # ⚠️ IMPORTANT: Do not hardcode credentials here!
    # Use .env file or environment variables instead
    BASE_URL = os.getenv("CONFLUENCE_URL")
    USERNAME = os.getenv("CONFLUENCE_USERNAME")
    API_TOKEN = os.getenv("CONFLUENCE_API_TOKEN")
    SPACE_KEY = os.getenv("CONFLUENCE_SPACE_KEY") or os.getenv("CONFLUENCE_DEFAULT_SPACE")
    
    # Validate required credentials
    if not BASE_URL:
        print("Error: CONFLUENCE_URL environment variable is required")
        exit(1)
    if not USERNAME:
        print("Error: CONFLUENCE_USERNAME environment variable is required")
        exit(1)
    if not API_TOKEN:
        print("Error: CONFLUENCE_API_TOKEN environment variable is required")
        exit(1)
    if not SPACE_KEY:
        print("Error: CONFLUENCE_SPACE_KEY or CONFLUENCE_DEFAULT_SPACE environment variable is required")
        exit(1)
    
    # Create tester instance
    tester = ConfluenceTester(BASE_URL, USERNAME, API_TOKEN)
    
    # Run full test suite
    success = tester.run_full_test(SPACE_KEY, cleanup=True)
    
    # Exit with appropriate code
    exit(0 if success else 1)


if __name__ == "__main__":
    main()

