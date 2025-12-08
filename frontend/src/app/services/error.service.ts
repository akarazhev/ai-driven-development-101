import { Injectable, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

export interface ErrorMessage {
  message: string;
  type: 'error' | 'warning' | 'info';
  timestamp: Date;
}

@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  private errors = signal<ErrorMessage[]>([]);
  errors$ = this.errors.asReadonly();

  addError(message: string, type: 'error' | 'warning' | 'info' = 'error') {
    const error: ErrorMessage = {
      message,
      type,
      timestamp: new Date()
    };
    this.errors.update(errors => [...errors, error]);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
      this.removeError(error);
    }, 5000);
  }

  removeError(error: ErrorMessage) {
    this.errors.update(errors => errors.filter(e => e !== error));
  }

  clearAll() {
    this.errors.set([]);
  }

  extractErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      const body = error.error;
      
      if (body?.message) {
        return body.message;
      }
      
      if (body?.detail) {
        return body.detail;
      }
      
      if (body?.errors) {
        const errorMessages = Object.values(body.errors) as string[];
        return errorMessages.join(', ');
      }
      
      if (error.status === 0) {
        return 'Unable to connect to the server. Please check your connection.';
      }
      
      if (error.status === 404) {
        return 'The requested resource was not found.';
      }
      
      if (error.status === 400) {
        return 'Invalid request. Please check your input.';
      }
      
      if (error.status === 500) {
        return 'An internal server error occurred. Please try again later.';
      }
      
      return `Error ${error.status}: ${error.statusText || 'Unknown error'}`;
    }
    
    if (error instanceof Error) {
      return error.message;
    }
    
    return 'An unexpected error occurred. Please try again.';
  }

  handleError(error: unknown, defaultMessage: string = 'An error occurred') {
    const message = this.extractErrorMessage(error) || defaultMessage;
    this.addError(message, 'error');
    console.error('Error handled:', error);
    return message;
  }
}

