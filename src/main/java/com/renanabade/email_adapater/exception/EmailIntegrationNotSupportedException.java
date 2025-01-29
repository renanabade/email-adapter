package com.renanabade.email_adapater.exception;

public class EmailIntegrationNotSupportedException extends RuntimeException {
    public EmailIntegrationNotSupportedException(String message) {
        super(message);
    }
}
