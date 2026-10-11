package com.renanabade.email_adapter.config;

import com.renanabade.email_adapter.services.impl.EmailAwsService;
import com.renanabade.email_adapter.services.impl.EmailOciService;
import com.renanabade.email_adapter.services.strategy.EmailSendingStrategy;
import com.renanabade.email_adapter.util.EmailLogger;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class EmailStrategyConfig {

    @Bean
    public EmailSendingStrategy emailSendingStrategy(MailProperties mailProperties, EmailLogger emailLogger) {
        return switch (mailProperties.integracao()) {
            case AWS -> new EmailAwsService(emailLogger);
            case OCI -> new EmailOciService(emailLogger);
        };
    }
}
