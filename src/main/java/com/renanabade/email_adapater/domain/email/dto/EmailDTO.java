package com.renanabade.email_adapater.domain.email.dto;

public record EmailDTO(String recipientEmail, String recipientName, String senderEmail, String subject, String body) {
}
