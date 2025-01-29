package com.renanabade.email_adapter.services.impl;

import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.exception.EmailIntegrationNotSupportedException;
import com.renanabade.email_adapter.util.EmailLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private EmailLogger emailLogger;

    @Mock
    private EmailAwsService emailAwsService;

    @Mock
    private EmailOciService emailOciService;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService("AWS", emailLogger);
        ReflectionTestUtils.setField(emailService, "emailSendingStrategy", emailAwsService);
    }

    @Test
    void sendEmail_UsingAwsIntegration_ShouldCallAwsService() {
        EmailDTO emailDTO = new EmailDTO("recipient@example.com", "Recipient Name", "sender@example.com", "Test Subject", "Test Body");

        emailService.sendEmail(emailDTO);
        verify(emailAwsService, times(1)).sendEmail(emailDTO);
    }

    @Test
    void sendEmail_UsingOciIntegration_ShouldCallOciService() {
        emailService = new EmailService("OCI", emailLogger);
        ReflectionTestUtils.setField(emailService, "emailSendingStrategy", emailOciService);

        EmailDTO emailDTO = new EmailDTO("recipient@example.com", "Recipient Name", "sender@example.com", "Test Subject", "Test Body");

        emailService.sendEmail(emailDTO);
        verify(emailOciService, times(1)).sendEmail(emailDTO);
    }

    @Test
    void sendEmail_InvalidIntegration_ShouldThrowException() {
        assertThrows(EmailIntegrationNotSupportedException.class, () -> new EmailService("INVALID", emailLogger));
    }
}