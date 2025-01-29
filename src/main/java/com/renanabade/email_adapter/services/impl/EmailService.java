package com.renanabade.email_adapter.services.impl;

import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.exception.EmailIntegrationNotSupportedException;
import com.renanabade.email_adapter.services.strategy.EmailSendingStrategy;
import com.renanabade.email_adapter.util.EmailLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements EmailSendingStrategy {

    private final EmailSendingStrategy emailSendingStrategy;

    @Autowired
    public EmailService(@Value("${mail.integracao}") String integracao, EmailLogger logger) {

        if ("AWS".equalsIgnoreCase(integracao)) {
            this.emailSendingStrategy = new EmailAwsService(logger);
        } else if ("OCI".equalsIgnoreCase(integracao)) {
            this.emailSendingStrategy = new EmailOciService(logger);
        } else {
            throw new EmailIntegrationNotSupportedException("Wrong value for mail.integracao");
        }
    }

    public void sendEmail(EmailDTO email)  {
            emailSendingStrategy.sendEmail(email);
    }
}
