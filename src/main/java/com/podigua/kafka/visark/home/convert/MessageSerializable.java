package com.podigua.kafka.visark.home.convert;

/**
 * 消息可序列化
 *
 * @author podigua
 * @date 2025/06/09
 */
public interface MessageSerializable {
    byte[] serialize(String message);
}
