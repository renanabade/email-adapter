package com.renanabade.email_adapater.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerialization {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serializeToJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

}