package com.podigua.kafka.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 **/
public class MapperUtils {

    public static String writeValue(Object object) {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = mapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,true);
        return mapper;
    }

    /**
     * 转换
     *
     * @param object
     * @param clazz
     * @param <T>c
     * @return
     */
    public static <T> T convertValue(Object object, Class<T> clazz) {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = mapper();
        return mapper.convertValue(object, clazz);
    }

    /**
     * 转换价值
     *
     * @param value 价值
     * @param clazz clazz
     * @return {@link T}
     */
    public static <T> T readValue(String value, Class<T> clazz) {
        if (value == null) {
            return null;
        }
        ObjectMapper mapper = mapper();
        try {
            return mapper.readValue(value, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
