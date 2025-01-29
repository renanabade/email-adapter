package com.renanabade.email_adapter.services.impl;

import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.exception.EmailSendingException;
import com.renanabade.email_adapter.exception.EmailValidationException;
import com.renanabade.email_adapter.util.EmailLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailOciServiceTest {

    @Mock
    private EmailLogger emailLogger;

    @InjectMocks
    private EmailOciService emailOciService;

    @BeforeEach
    void setUp() {
        emailOciService = new EmailOciService(emailLogger);
    }

    @Test
    void sendEmail_SuccessfulSend_ShouldLogSuccess() {
        EmailDTO emailDTO = new EmailDTO("recipient@example.com", "Recipient Name", "sender@example.com", "Test Subject", "Test Body");

        assertDoesNotThrow(() -> emailOciService.sendEmail(emailDTO));
        verify(emailLogger, times(1)).successLog(emailDTO);
        verify(emailLogger, never()).failLog(any());
    }

    @Test
    void sendEmail_InvalidEmail_ShouldThrowValidationException() {
        EmailDTO emailDTO = new EmailDTO("invalid-email", "Recipient Name", "sender@example.com", "Test Subject", "Test Body");

        assertThrows(EmailValidationException.class, () -> emailOciService.sendEmail(emailDTO));
        verify(emailLogger, never()).successLog(any());
        verify(emailLogger, never()).failLog(any());
    }

    @Test
    void sendEmail_ExceptionOccurs_ShouldLogFailureAndThrowException() {
        EmailDTO emailDTO = new EmailDTO("recipient@example.com", "Recipient Name", "sender@example.com", "Test Subject", "Test Body");

        doThrow(new RuntimeException("OCI Service Down"))
                .when(emailLogger).successLog(emailDTO);

        assertThrows(EmailSendingException.class, () -> emailOciService.sendEmail(emailDTO));
        verify(emailLogger, times(1)).failLog(emailDTO);
    }
}
