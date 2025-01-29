package com.renanabade.email_adapter.controller;

import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.exception.EmailSendingException;
import com.renanabade.email_adapter.services.impl.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @Test
    public void testInsert_Success() {
        EmailDTO emailData = new EmailDTO(
                "recipient@example.com",
                "Recipient Name",
                "sender@example.com",
                "Subject",
                "Body"
        );

        doNothing().when(emailService).sendEmail(emailData);

        ResponseEntity<Void> response = emailController.send(emailData);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(emailService, times(1)).sendEmail(emailData);
    }

    @Test
    public void testInsert_EmailSendingException() {
        EmailDTO emailData = new EmailDTO(
                "recipient@example.com",
                "Recipient Name",
                "sender@example.com",
                "Subject",
                "Body"
        );

        doThrow(new EmailSendingException("Email sending error")).when(emailService).sendEmail(emailData);

        ResponseEntity<Void> response = emailController.send(emailData);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());

        verify(emailService, times(1)).sendEmail(emailData);
    }

    @Test
    public void testInsert_GenericException() {
        EmailDTO emailData = new EmailDTO(
                "recipient@example.com",
                "Recipient Name",
                "sender@example.com",
                "Subject",
                "Body"
        );

        doThrow(new RuntimeException("Internal error")).when(emailService).sendEmail(emailData);

        ResponseEntity<Void> response = emailController.send(emailData);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        verify(emailService, times(1)).sendEmail(emailData);
    }
}
