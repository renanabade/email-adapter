package com.renanabade.email_adapter.controller;

import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.services.strategy.EmailSendingStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emails")
public class EmailController {

    private final EmailSendingStrategy emailSendingStrategy;

    public EmailController(EmailSendingStrategy emailSendingStrategy) {
        this.emailSendingStrategy = emailSendingStrategy;
    }

    @PostMapping
    public ResponseEntity<Void> send(@RequestBody EmailDTO emailData) {
        emailSendingStrategy.sendEmail(emailData);
        return ResponseEntity.noContent().build();
    }
}
