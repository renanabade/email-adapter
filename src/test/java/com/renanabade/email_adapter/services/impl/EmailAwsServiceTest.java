package com.renanabade.email_adapter.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.exception.EmailSendingException;
import com.renanabade.email_adapter.exception.EmailValidationException;
import com.renanabade.email_adapter.util.EmailLogger;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailAwsServiceTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Mock
    private EmailLogger emailLogger;

    private EmailAwsService emailAwsService;

    @BeforeEach
    void setUp() {
        emailAwsService = new EmailAwsService(emailLogger, VALIDATOR, new ObjectMapper());
    }

    @Test
    void sendEmail_SuccessfulSend_ShouldLogSuccess() {
        EmailDTO emailDTO = new EmailDTO("recipient@example.com", "Recipient Name", "sender@example.com", "Test Subject", "Test Body");

        assertDoesNotThrow(() -> emailAwsService.sendEmail(emailDTO));
        verify(emailLogger, times(1)).successLog(emailDTO);
        verify(emailLogger, never()).failLog(any());
    }

    @Test
    void sendEmail_InvalidEmail_ShouldThrowValidationException() {
        EmailDTO emailDTO = new EmailDTO("invalid-email", "Recipient Name", "sender@example.com", "Test Subject", "Test Body");

        assertThrows(EmailValidationException.class, () -> emailAwsService.sendEmail(emailDTO));
        verify(emailLogger, never()).successLog(any());
        verify(emailLogger, never()).failLog(any());
    }

    @Test
    void sendEmail_RecipientWithin45Characters_ShouldBeAccepted() {
        // 42 characters: accepted by AWS (max 45) but rejected by OCI (max 40)
        String recipient = "a".repeat(30) + "@example.com";
        EmailDTO emailDTO = new EmailDTO(recipient, "Recipient Name", "sender@example.com", "Test Subject", "Test Body");

        assertDoesNotThrow(() -> emailAwsService.sendEmail(emailDTO));
    }

    @Test
    void sendEmail_SerializationFails_ShouldLogFailureAndThrowSendingException() throws JsonProcessingException {
        ObjectMapper failingMapper = mock(ObjectMapper.class);
        when(failingMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("AWS payload error") {});
        emailAwsService = new EmailAwsService(emailLogger, VALIDATOR, failingMapper);
        EmailDTO emailDTO = new EmailDTO("recipient@example.com", "Recipient Name", "sender@example.com", "Test Subject", "Test Body");

        EmailSendingException exception = assertThrows(EmailSendingException.class, () -> emailAwsService.sendEmail(emailDTO));
        assertEquals("Communication instability with AWS", exception.getMessage());
        verify(emailLogger, times(1)).failLog(emailDTO);
        verify(emailLogger, never()).successLog(any());
    }
}
