package com.renanabade.email_adapter.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renanabade.email_adapter.domain.email.constants.EmailProvider;
import com.renanabade.email_adapter.domain.email.dto.EmailOciDTO;
import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.services.strategy.AbstractEmailSendingStrategy;
import com.renanabade.email_adapter.util.EmailLogger;
import jakarta.validation.Validator;

public class EmailOciService extends AbstractEmailSendingStrategy<EmailOciDTO> {

    public EmailOciService(EmailLogger emailLogger, Validator validator, ObjectMapper objectMapper) {
        super(emailLogger, validator, objectMapper);
    }

    @Override
    protected EmailProvider provider() {
        return EmailProvider.OCI;
    }

    @Override
    protected EmailOciDTO toProviderPayload(EmailDTO emailData) {
        return new EmailOciDTO(
                emailData.recipientEmail(),
                emailData.recipientName(),
                emailData.senderEmail(),
                emailData.subject(),
                emailData.body()
        );
    }
}
