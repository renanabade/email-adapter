package com.renanabade.email_adapter.exception;

public class EmailIntegrationNotSupportedException extends RuntimeException {
    public EmailIntegrationNotSupportedException(String message) {
        super(message);
    }
}
