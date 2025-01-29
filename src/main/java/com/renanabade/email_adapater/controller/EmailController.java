package com.renanabade.email_adapater.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.renanabade.email_adapater.domain.email.dto.EmailDTO;
import com.renanabade.email_adapater.exception.EmailSendingException;
import com.renanabade.email_adapater.services.impl.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<EmailDTO> insert(@RequestBody EmailDTO emailData) {
        try {

            this.emailService.sendEmail(emailData);
            return ResponseEntity.noContent().build();

        }  catch (EmailSendingException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            System.out.println("Error: {}" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
