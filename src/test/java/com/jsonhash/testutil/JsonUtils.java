package com.jsonhash.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Object givenJson(String file) throws IOException {
        return mapper.readValue(ResourceUtils.getTestResource("/json/" + file), Object.class);
    }
}
