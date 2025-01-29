package com.renanabade.email_adapter.services.impl;

import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.domain.email.dto.EmailOciDTO;
import com.renanabade.email_adapter.exception.EmailSendingException;
import com.renanabade.email_adapter.exception.EmailValidationException;
import com.renanabade.email_adapter.services.strategy.EmailSendingStrategy;
import com.renanabade.email_adapter.util.EmailLogger;
import com.renanabade.email_adapter.util.JsonSerialization;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EmailOciService implements EmailSendingStrategy {

    private final EmailLogger logger;
    private final Validator validator;

    public EmailOciService(EmailLogger logger) {
        this.logger = logger;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Override
    public void sendEmail(EmailDTO emailData) {
        try {
            EmailOciDTO newEmailData = new EmailOciDTO(
                    emailData.recipientEmail(),
                    emailData.recipientName(),
                    emailData.senderEmail(),
                    emailData.subject(),
                    emailData.body()
            );

            Set<ConstraintViolation<EmailOciDTO>> violations = validator.validate(newEmailData);
            if (!violations.isEmpty()) {
                for (ConstraintViolation<EmailOciDTO> violation : violations) {
                    System.out.println("Validation error: " + violation.getMessage());
                }
                throw new EmailValidationException("Validation failed for email data");
            }

            System.out.println(JsonSerialization.serializeToJson(newEmailData));
            logger.successLog(emailData);

        } catch (EmailValidationException e) {
            throw e;
        }  catch (Exception e) {
            logger.failLog(emailData);
            throw new EmailSendingException("Communication Instability with AWS");
        }
    }
}