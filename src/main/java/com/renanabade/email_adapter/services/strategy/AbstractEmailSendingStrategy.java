package com.renanabade.email_adapter.services.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renanabade.email_adapter.domain.email.constants.EmailProvider;
import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.exception.EmailSendingException;
import com.renanabade.email_adapter.exception.EmailValidationException;
import com.renanabade.email_adapter.util.EmailLogger;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Common sending flow shared by every provider: map the request to the
 * provider payload, validate it, serialize it and record the result.
 * Subclasses only describe how the request is mapped to their own DTO.
 *
 * @param <T> provider specific payload
 */
public abstract class AbstractEmailSendingStrategy<T> implements EmailSendingStrategy {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final EmailLogger emailLogger;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    protected AbstractEmailSendingStrategy(EmailLogger emailLogger, Validator validator, ObjectMapper objectMapper) {
        this.emailLogger = emailLogger;
        this.validator = validator;
        this.objectMapper = objectMapper;
    }

    protected abstract EmailProvider provider();

    protected abstract T toProviderPayload(EmailDTO emailData);

    @Override
    public final void sendEmail(EmailDTO emailData) {
        T payload = toProviderPayload(emailData);
        validate(payload);

        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            emailLogger.failLog(emailData);
            throw new EmailSendingException("Communication instability with " + provider(), e);
        }

        // The challenge does not call the real provider: sending means serializing the payload and logging it.
        log.info("Email sent through {}: {}", provider(), json);
        emailLogger.successLog(emailData);
    }

    /**
     * Provider DTOs may name fields differently from the request (e.g. AWS uses
     * "content" for the request "body"). Override to report errors using the
     * names the client actually sent.
     */
    protected String toRequestField(String payloadField) {
        return payloadField;
    }

    private void validate(T payload) {
        Set<ConstraintViolation<T>> violations = validator.validate(payload);
        if (violations.isEmpty()) {
            return;
        }

        List<EmailValidationException.FieldValidationError> errors = violations.stream()
                .map(v -> new EmailValidationException.FieldValidationError(
                        toRequestField(v.getPropertyPath().toString()), v.getMessage()))
                .sorted(Comparator.comparing(EmailValidationException.FieldValidationError::field)
                        .thenComparing(EmailValidationException.FieldValidationError::message))
                .toList();
        throw new EmailValidationException("Validation failed for " + provider() + " email data", errors);
    }
}
