package com.renanabade.email_adapter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renanabade.email_adapter.services.impl.EmailAwsService;
import com.renanabade.email_adapter.services.impl.EmailOciService;
import com.renanabade.email_adapter.services.strategy.EmailSendingStrategy;
import com.renanabade.email_adapter.util.EmailLogger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class EmailStrategyConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
            .withUserConfiguration(EmailStrategyConfig.class)
            .withBean(EmailLogger.class, () -> mock(EmailLogger.class))
            .withBean(ObjectMapper.class, ObjectMapper::new);

    @Test
    void awsIntegration_ShouldProvideAwsStrategy() {
        contextRunner.withPropertyValues("mail.integracao=AWS")
                .run(context -> assertThat(context.getBean(EmailSendingStrategy.class))
                        .isInstanceOf(EmailAwsService.class));
    }

    @Test
    void ociIntegration_ShouldProvideOciStrategy() {
        contextRunner.withPropertyValues("mail.integracao=OCI")
                .run(context -> assertThat(context.getBean(EmailSendingStrategy.class))
                        .isInstanceOf(EmailOciService.class));
    }

    @Test
    void integrationValue_ShouldBeCaseInsensitive() {
        contextRunner.withPropertyValues("mail.integracao=aws")
                .run(context -> assertThat(context.getBean(EmailSendingStrategy.class))
                        .isInstanceOf(EmailAwsService.class));
    }

    @Test
    void unknownIntegration_ShouldFailOnStartup() {
        contextRunner.withPropertyValues("mail.integracao=SMTP")
                .run(context -> assertThat(context).hasFailed()
                        .getFailure()
                        .hasStackTraceContaining(BindException.class.getName())
                        .hasStackTraceContaining("mail.integracao"));
    }

    @Test
    void missingIntegration_ShouldFailOnStartup() {
        contextRunner.run(context -> assertThat(context).hasFailed()
                .getFailure().hasStackTraceContaining("mail.integracao"));
    }
}
