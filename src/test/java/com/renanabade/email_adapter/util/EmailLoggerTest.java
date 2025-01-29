package com.renanabade.email_adapter.util;

import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.domain.email.model.EmailLog;
import com.renanabade.email_adapter.repositories.EmailLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailLoggerTest {

    @Mock
    private EmailLogRepository emailLogRepository;

    @InjectMocks
    private EmailLogger emailLogger;

    private EmailDTO emailDTO;

    @BeforeEach
    void setUp() {
        emailDTO = new EmailDTO("recipient@example.com", "Recipient Name", "sender@example.com", "Test Subject", "Test Body");
    }

    @Test
    void successLog_ShouldSaveSuccessLog() {
        emailLogger.successLog(emailDTO);
        verify(emailLogRepository, times(1)).save(any(EmailLog.class));
    }

    @Test
    void failLog_ShouldSaveFailLog() {
        emailLogger.failLog(emailDTO);
        verify(emailLogRepository, times(1)).save(any(EmailLog.class));
    }
}
