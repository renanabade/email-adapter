package com.renanabade.email_adapater.domain.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailAwsDTO(
        @NotBlank(message = "Recipient's email is required")
        @Email(message = "Invalid email format")
        @Size(max = 45, message = "Recipient's email must be at most 45 characters")
        String recipient,

        @NotBlank(message = "Recipient's name is required")
        @Size(max = 60, message = "Recipient's name must be at most 60 characters")
        String recipientName,

        @NotBlank(message = "Sender's email is required")
        @Email(message = "Invalid email format")
        @Size(max = 45, message = "Sender's email must be at most 45 characters")
        String sender,

        @NotBlank(message = "Subject is required")
        @Size(max = 120, message = "Subject must be at most 120 characters")
        String subject,

        @NotBlank(message = "Content is required")
        @Size(max = 256, message = "Content must be at most 256 characters")
        String content
) {}
