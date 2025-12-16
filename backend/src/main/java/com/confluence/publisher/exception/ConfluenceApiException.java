package com.confluence.publisher.exception;

import lombok.Getter;

@Getter
public class ConfluenceApiException extends RuntimeException {
    
    private final int statusCode;
    private final String errorResponse;
    private final boolean retryable;
    
    public ConfluenceApiException(String message) {
        super(message);
        this.statusCode = 0;
        this.errorResponse = null;
        this.retryable = false;
    }
    
    public ConfluenceApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorResponse = null;
        this.retryable = isRetryableStatusCode(statusCode);
    }
    
    public ConfluenceApiException(String message, int statusCode, String errorResponse) {
        super(message);
        this.statusCode = statusCode;
        this.errorResponse = errorResponse;
        this.retryable = isRetryableStatusCode(statusCode);
    }
    
    public ConfluenceApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.errorResponse = null;
        this.retryable = false;
    }
    
    public ConfluenceApiException(String message, int statusCode, String errorResponse, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorResponse = errorResponse;
        this.retryable = isRetryableStatusCode(statusCode);
    }
    
    private static boolean isRetryableStatusCode(int statusCode) {
        return statusCode == 429 || (statusCode >= 500 && statusCode < 600);
    }
    
    public boolean isUnauthorized() {
        return statusCode == 401;
    }
    
    public boolean isForbidden() {
        return statusCode == 403;
    }
    
    public boolean isNotFound() {
        return statusCode == 404;
    }
    
    public boolean isRateLimited() {
        return statusCode == 429;
    }
    
    public boolean isServerError() {
        return statusCode >= 500 && statusCode < 600;
    }
}
