package com.podigua.kafka.core.utils;

import java.util.UUID;

/**
 * uuid 工具类
 *
 * @author podigua
 * @date 2024/03/20
 */
public class UUIDUtils {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
