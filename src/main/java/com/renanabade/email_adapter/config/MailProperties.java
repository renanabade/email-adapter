package com.renanabade.email_adapter.config;

import com.renanabade.email_adapter.domain.email.constants.EmailProvider;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Binds mail.integracao to {@link EmailProvider} so that a missing or unknown
 * value stops the application on startup instead of failing on the first request.
 */
@Validated
@ConfigurationProperties(prefix = "mail")
public record MailProperties(@NotNull EmailProvider integracao) {
}
