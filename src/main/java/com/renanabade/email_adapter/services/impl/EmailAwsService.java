package com.renanabade.email_adapter.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renanabade.email_adapter.domain.email.constants.EmailProvider;
import com.renanabade.email_adapter.domain.email.dto.EmailAwsDTO;
import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.services.strategy.AbstractEmailSendingStrategy;
import com.renanabade.email_adapter.util.EmailLogger;
import jakarta.validation.Validator;

import java.util.Map;

public class EmailAwsService extends AbstractEmailSendingStrategy<EmailAwsDTO> {

    private static final Map<String, String> REQUEST_FIELDS = Map.of(
            "recipient", "recipientEmail",
            "sender", "senderEmail",
            "content", "body"
    );

    public EmailAwsService(EmailLogger emailLogger, Validator validator, ObjectMapper objectMapper) {
        super(emailLogger, validator, objectMapper);
    }

    @Override
    protected EmailProvider provider() {
        return EmailProvider.AWS;
    }

    @Override
    protected EmailAwsDTO toProviderPayload(EmailDTO emailData) {
        return new EmailAwsDTO(
                emailData.recipientEmail(),
                emailData.recipientName(),
                emailData.senderEmail(),
                emailData.subject(),
                emailData.body()
        );
    }

    @Override
    protected String toRequestField(String payloadField) {
        return REQUEST_FIELDS.getOrDefault(payloadField, payloadField);
    }
}
