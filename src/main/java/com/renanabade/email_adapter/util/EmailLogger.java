package com.renanabade.email_adapter.util;

import com.renanabade.email_adapter.domain.email.constants.LogStatus;
import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.domain.email.model.EmailLog;
import com.renanabade.email_adapter.repositories.EmailLogRepository;

public class EmailLogger {

    EmailLogRepository emailLogRepository;

    public EmailLogger(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    public void successLog (EmailDTO emailData) {
        EmailLog log = new EmailLog(emailData.recipientEmail(), emailData.senderEmail(),
                emailData.subject(), LogStatus.SUCCESS);
        emailLogRepository.save(log);

    }

    public void failLog (EmailDTO emailData) {
        EmailLog log = new EmailLog(emailData.recipientEmail(), emailData.senderEmail(),
                emailData.subject(), LogStatus.FAIL);
        emailLogRepository.save(log);
    }

}
