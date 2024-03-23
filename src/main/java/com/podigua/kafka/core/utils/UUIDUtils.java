package com.podigua.kafka.core.utils;

import java.util.UUID;

/**
 * uuid 工具类
 *
 * @author podigua
 * @date 2024/03/20
 */
public class UUIDUtils {
    /**
     * UUID
     *
     * @return {@link String}
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 组 ID
     *
     * @return {@link Object}
     */
    public static Object groupId() {
        return "kafka-visark-".concat(UUIDUtils.uuid());
    }
}
