package com.renanabade.email_adapter.controller;

import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import com.renanabade.email_adapter.exception.EmailSendingException;
import com.renanabade.email_adapter.services.strategy.EmailSendingStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmailController.class)
class EmailControllerTest {

    private static final String VALID_BODY = """
            {
              "recipientEmail": "recipient@example.com",
              "recipientName": "Recipient Name",
              "senderEmail": "sender@example.com",
              "subject": "Subject",
              "body": "Body"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailSendingStrategy emailSendingStrategy;

    @Test
    void send_Success_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(post("/emails").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isNoContent());

        verify(emailSendingStrategy, times(1)).sendEmail(new EmailDTO(
                "recipient@example.com", "Recipient Name", "sender@example.com", "Subject", "Body"));
    }

    @Test
    void send_ProviderFailure_ShouldReturnServiceUnavailable() throws Exception {
        doThrow(new EmailSendingException("Communication instability with AWS"))
                .when(emailSendingStrategy).sendEmail(any());

        mockMvc.perform(post("/emails").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.detail").value("Communication instability with AWS"));
    }

    @Test
    void send_UnexpectedError_ShouldReturnInternalServerErrorWithoutDetails() throws Exception {
        doThrow(new IllegalStateException("database password is wrong"))
                .when(emailSendingStrategy).sendEmail(any());

        mockMvc.perform(post("/emails").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred"))
                .andExpect(content().string(not(containsString("database password"))))
                .andExpect(content().string(not(containsString("IllegalStateException"))));
    }

    @Test
    void send_MalformedJson_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/emails").contentType(MediaType.APPLICATION_JSON).content("{ not json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

        verifyNoInteractions(emailSendingStrategy);
    }
}
