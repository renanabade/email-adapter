package com.renanabade.email_adapter.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.renanabade.email_adapter.domain.email.dto.EmailDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonSerializationTest {

    @Test
    void serializeToJson_ValidObject_ShouldReturnJsonString() throws JsonProcessingException {
        EmailDTO emailDTO = new EmailDTO("recipient@example.com", "Recipient Name", "sender@example.com", "Test Subject", "Test Body");

        String json = JsonSerialization.serializeToJson(emailDTO);

        assertNotNull(json);
        assertTrue(json.contains("recipient@example.com"));
        assertTrue(json.contains("Test Subject"));
    }

    @Test
    void serializeToJson_NullObject_ShouldThrowException() {
        assertThrows(JsonProcessingException.class, () -> JsonSerialization.serializeToJson(null));
    }
}