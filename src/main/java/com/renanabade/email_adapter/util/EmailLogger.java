package com.renanabade.email_adapter.util;

import com.renanabade.email_adapter.domain.email.constants.LogStatus;
import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.domain.email.model.EmailLog;
import com.renanabade.email_adapter.repositories.EmailLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persists the result of each sending attempt. Persistence is best effort:
 * a failure here is logged and never changes the outcome of the sending.
 */
public class EmailLogger {

    private static final Logger log = LoggerFactory.getLogger(EmailLogger.class);

    private final EmailLogRepository emailLogRepository;

    public EmailLogger(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    public void successLog(EmailDTO emailData) {
        save(emailData, LogStatus.SUCCESS);
    }

    public void failLog(EmailDTO emailData) {
        save(emailData, LogStatus.FAIL);
    }

    private void save(EmailDTO emailData, LogStatus status) {
        EmailLog emailLog = new EmailLog(emailData.recipientEmail(), emailData.senderEmail(),
                emailData.subject(), status);
        try {
            emailLogRepository.save(emailLog);
        } catch (RuntimeException e) {
            log.error("Could not persist {} email log for {}", status, emailData.recipientEmail(), e);
        }
    }
}
