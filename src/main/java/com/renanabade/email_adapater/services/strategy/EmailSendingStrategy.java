package com.renanabade.email_adapater.services.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.renanabade.email_adapater.domain.email.dto.EmailDTO;


public interface EmailSendingStrategy {
     void sendEmail (EmailDTO email) throws JsonProcessingException;
}
