package com.renanabade.email_adapter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renanabade.email_adapter.services.impl.EmailAwsService;
import com.renanabade.email_adapter.services.impl.EmailOciService;
import com.renanabade.email_adapter.services.strategy.EmailSendingStrategy;
import com.renanabade.email_adapter.util.EmailLogger;
import jakarta.validation.Validator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class EmailStrategyConfig {

    @Bean
    public EmailSendingStrategy emailSendingStrategy(MailProperties mailProperties, EmailLogger emailLogger,
                                                     Validator validator, ObjectMapper objectMapper) {
        return switch (mailProperties.integracao()) {
            case AWS -> new EmailAwsService(emailLogger, validator, objectMapper);
            case OCI -> new EmailOciService(emailLogger, validator, objectMapper);
        };
    }
}
