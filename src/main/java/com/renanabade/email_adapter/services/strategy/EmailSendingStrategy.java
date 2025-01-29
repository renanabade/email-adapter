package com.renanabade.email_adapter.services.strategy;

import com.renanabade.email_adapter.domain.email.dto.EmailDTO;


public interface EmailSendingStrategy {
     void sendEmail (EmailDTO email);
}
