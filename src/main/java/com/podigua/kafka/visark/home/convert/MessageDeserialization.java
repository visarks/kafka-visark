package com.podigua.kafka.visark.home.convert;

/**
 * 消息反序列化
 *
 * @author podigua
 * @date 2025/06/09
 */
public interface MessageDeserialization {
    /**
     * 反序列化
     *
     * @param bytes 字节
     * @return {@link String }
     */
    String deserialize(byte[] bytes);
}
