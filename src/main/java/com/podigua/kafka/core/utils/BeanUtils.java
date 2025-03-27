package com.podigua.kafka.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Bean 实用程序
 *
 * @author podigua
 * @date 2024/11/11
 */
public class BeanUtils {
    private static ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    }

    /**
     * 读取值
     *
     * @param value 价值
     * @param clazz 克拉兹
     * @return {@link T }
     */
    public static <T> T readValue(String value, Class<T> clazz) {
        if (value == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        try {
            return mapper.readValue(value, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取值
     *
     * @param value 价值
     * @return {@link T }
     */
    public static <T> T readValue(String value, TypeReference<T> reference) {
        try {
            return MAPPER.readValue(value, reference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将值写入字符串
     *
     * @param value 价值
     * @return {@link String }
     */
    public static String writeValueAsString(Object value) {
        if (value == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.writer().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
