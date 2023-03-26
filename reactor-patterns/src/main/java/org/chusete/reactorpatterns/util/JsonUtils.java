package org.chusete.reactorpatterns.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties("_jacksonMapper")
public class JsonUtils {
    private static ObjectMapper _jacksonMapper = new ObjectMapper();

    public static String toJson(Object o) {
        try {
            return _jacksonMapper.writer().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Error converting %s to JSON: ", o.getClass().getSimpleName()), e);
        }
    }
}
