package com.renanabade.email_adapter.domain.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailOciDTO(
        @NotBlank(message = "Recipient's email is required")
        @Email(message = "Invalid email format")
        @Size(max = 40, message = "Recipient's email must be at most 40 characters")
        String recipientEmail,

        @NotBlank(message = "Recipient's name is required")
        @Size(max = 50, message = "Recipient's name must be at most 50 characters")
        String recipientName,

        @NotBlank(message = "Sender's email is required")
        @Email(message = "Invalid email format")
        @Size(max = 40, message = "Sender's email must be at most 40 characters")
        String senderEmail,

        @NotBlank(message = "Subject is required")
        @Size(max = 100, message = "Subject must be at most 100 characters")
        String subject,

        @NotBlank(message = "Body is required")
        @Size(max = 250, message = "Body must be at most 250 characters")
        String body
) {}
