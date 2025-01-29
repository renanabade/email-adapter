package com.renanabade.email_adapter.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerialization {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serializeToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            throw new JsonProcessingException("Cannot serialize null object") {};
        }
        return objectMapper.writeValueAsString(object);
    }

}