package com.renanabade.email_adapter.util;

import com.renanabade.email_adapter.domain.email.constants.LogStatus;
import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.domain.email.model.EmailLog;
import com.renanabade.email_adapter.repositories.EmailLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        ArgumentCaptor<EmailLog> captor = ArgumentCaptor.forClass(EmailLog.class);
        verify(emailLogRepository, times(1)).save(captor.capture());
        assertEquals(LogStatus.SUCCESS, captor.getValue().getStatus());
        assertEquals("recipient@example.com", captor.getValue().getRecipient());
    }

    @Test
    void failLog_ShouldSaveFailLog() {
        emailLogger.failLog(emailDTO);

        ArgumentCaptor<EmailLog> captor = ArgumentCaptor.forClass(EmailLog.class);
        verify(emailLogRepository, times(1)).save(captor.capture());
        assertEquals(LogStatus.FAIL, captor.getValue().getStatus());
    }

    @Test
    void successLog_RepositoryUnavailable_ShouldNotThrow() {
        when(emailLogRepository.save(any(EmailLog.class)))
                .thenThrow(new DataAccessResourceFailureException("MongoDB is down"));

        assertDoesNotThrow(() -> emailLogger.successLog(emailDTO));
    }
}
