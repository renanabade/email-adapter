package com.renanabade.email_adapter.exception;

import java.util.List;

public class EmailValidationException extends RuntimeException {

    private final List<FieldValidationError> errors;

    public EmailValidationException(String message, List<FieldValidationError> errors) {
        super(message);
        this.errors = List.copyOf(errors);
    }

    public List<FieldValidationError> getErrors() {
        return errors;
    }

    public record FieldValidationError(String field, String message) {
    }
}
