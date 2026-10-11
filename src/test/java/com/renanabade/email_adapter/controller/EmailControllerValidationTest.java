package com.renanabade.email_adapter.controller;

import com.renanabade.email_adapter.config.EmailStrategyConfig;
import com.renanabade.email_adapter.util.EmailLogger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Runs the request through the real AWS strategy so the 400 response reflects
 * the actual DTO constraints, not a mocked exception.
 */
@WebMvcTest(EmailController.class)
@Import(EmailStrategyConfig.class)
@TestPropertySource(properties = "mail.integracao=AWS")
class EmailControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailLogger emailLogger;

    @Test
    void send_InvalidFields_ShouldReturnBadRequestListingEachField() throws Exception {
        String body = """
                {
                  "recipientEmail": "not-an-email",
                  "recipientName": "Recipient Name",
                  "senderEmail": "sender@example.com",
                  "subject": "",
                  "body": "Body"
                }
                """;

        mockMvc.perform(post("/emails").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Invalid email data"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.instance").value("/emails"))
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[0].field").value("recipientEmail"))
                .andExpect(jsonPath("$.errors[0].message").value("Invalid email format"))
                .andExpect(jsonPath("$.errors[1].field").value("subject"))
                .andExpect(jsonPath("$.errors[1].message").value("Subject is required"));

        verifyNoInteractions(emailLogger);
    }
}
