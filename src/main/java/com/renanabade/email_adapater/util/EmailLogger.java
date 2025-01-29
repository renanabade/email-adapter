package com.renanabade.email_adapater.util;

import com.renanabade.email_adapater.domain.email.constants.LogStatus;
import com.renanabade.email_adapater.domain.email.dto.EmailDTO;
import com.renanabade.email_adapater.domain.email.model.EmailLog;
import com.renanabade.email_adapater.repositories.EmailLogRepository;

public class EmailLogger {

    EmailLogRepository emailLogRepository;

    public EmailLogger(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    public void successLog (EmailDTO emailData) {
        EmailLog log = new EmailLog(emailData.recipientEmail(), emailData.senderEmail(),
                emailData.subject(), emailData.body(), LogStatus.SUCCESS);
        emailLogRepository.save(log);

    }

    public void failLog (EmailDTO emailData) {
        EmailLog log = new EmailLog(emailData.recipientEmail(), emailData.senderEmail(),
                emailData.subject(), emailData.body(), LogStatus.FAIL);
        emailLogRepository.save(log);
    }

}
