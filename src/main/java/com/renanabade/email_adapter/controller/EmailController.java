package com.renanabade.email_adapter.controller;

import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.exception.EmailSendingException;
import com.renanabade.email_adapter.exception.EmailValidationException;
import com.renanabade.email_adapter.services.strategy.EmailSendingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emails")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    private final EmailSendingStrategy emailSendingStrategy;

    public EmailController(EmailSendingStrategy emailSendingStrategy) {
        this.emailSendingStrategy = emailSendingStrategy;
    }

    @PostMapping
    public ResponseEntity<Void> send(@RequestBody EmailDTO emailData) {
        try {
            this.emailSendingStrategy.sendEmail(emailData);
            return ResponseEntity.noContent().build();

        } catch (EmailSendingException e) {
            logger.error("Email sending failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();

        } catch (EmailValidationException e) {
            logger.error("Email validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
