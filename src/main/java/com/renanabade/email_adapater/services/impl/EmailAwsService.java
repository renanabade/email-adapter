package com.renanabade.email_adapater.services.impl;

import com.renanabade.email_adapater.domain.email.dto.EmailAwsDTO;
import com.renanabade.email_adapater.domain.email.dto.EmailDTO;
import com.renanabade.email_adapater.exception.EmailSendingException;
import com.renanabade.email_adapater.services.strategy.EmailSendingStrategy;
import com.renanabade.email_adapater.util.EmailLogger;
import com.renanabade.email_adapater.util.JsonSerialization;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EmailAwsService implements EmailSendingStrategy {

    private final EmailLogger logger;
    private final Validator validator;

    @Autowired
    public EmailAwsService(EmailLogger logger) {
        this.logger = logger;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Override
    public void sendEmail(EmailDTO emailData) {
        try {

            EmailAwsDTO newEmailData = new EmailAwsDTO(
                    emailData.recipientEmail(),
                    emailData.recipientName(),
                    emailData.senderEmail(),
                    emailData.subject(),
                    emailData.body()
            );

            Set<ConstraintViolation<EmailAwsDTO>> violations = validator.validate(newEmailData);
            if (!violations.isEmpty()) {
                for (ConstraintViolation<EmailAwsDTO> violation : violations) {
                    System.out.println("Validation error: " + violation.getMessage());
                }
                throw new EmailSendingException("Validation failed for email data");
            }

            System.out.println(JsonSerialization.serializeToJson(newEmailData));
            logger.successLog(emailData);
        } catch (Exception e) {
            logger.failLog(emailData);
            throw new EmailSendingException("Communication Instability with AWS");
        }
    }
}