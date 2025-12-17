package com.confluence.publisher.exception;

import lombok.Getter;

@Getter
public class ConfluenceApiException extends RuntimeException {
    
    private final int statusCode;
    private final String responseBody;
    private final boolean retryable;
    
    public ConfluenceApiException(String message) {
        super(message);
        this.statusCode = 0;
        this.responseBody = null;
        this.retryable = false;
    }
    
    public ConfluenceApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.responseBody = null;
        this.retryable = false;
    }
    
    public ConfluenceApiException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.retryable = isRetryableStatusCode(statusCode);
    }
    
    public ConfluenceApiException(String message, int statusCode, String responseBody, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.retryable = isRetryableStatusCode(statusCode);
    }
    
    private static boolean isRetryableStatusCode(int statusCode) {
        return statusCode == 429 || (statusCode >= 500 && statusCode < 600);
    }
    
    public static ConfluenceApiException unauthorized(String message) {
        return new ConfluenceApiException("Unauthorized: " + message, 401, null);
    }
    
    public static ConfluenceApiException forbidden(String message) {
        return new ConfluenceApiException("Forbidden: " + message, 403, null);
    }
    
    public static ConfluenceApiException notFound(String message) {
        return new ConfluenceApiException("Not found: " + message, 404, null);
    }
    
    public static ConfluenceApiException rateLimited(String message) {
        return new ConfluenceApiException("Rate limited: " + message, 429, null);
    }
    
    public static ConfluenceApiException serverError(String message, int statusCode) {
        return new ConfluenceApiException("Server error: " + message, statusCode, null);
    }
}
