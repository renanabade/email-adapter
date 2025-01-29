package com.renanabade.email_adapater.config;

import com.renanabade.email_adapater.repositories.EmailLogRepository;
import com.renanabade.email_adapater.util.EmailLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public EmailLogger emailLogger(EmailLogRepository emailLogRepository) {
        return new EmailLogger(emailLogRepository);
    }
}