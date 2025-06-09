package com.podigua.kafka.visark.home.convert.serializable;

import com.podigua.kafka.visark.home.convert.MessageSerializable;
import org.springframework.util.StringUtils;

/**
 * 十六进制可序列化
 *
 * @author podigua
 * @date 2025/06/09
 */
public class HexSerializable implements MessageSerializable {
    public static HexSerializable DEFAULT = new HexSerializable();

    @Override
    public byte[] serialize(String message) {
        if (!StringUtils.hasText(message)) {
            return new byte[0];
        }
        String trimmed = message.replaceAll("\\s+", "");
        if (trimmed.length() % 2 != 0) {
            throw new IllegalArgumentException("The length of the Hex string must be even: " + trimmed);
        }
        int len = trimmed.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) Integer.parseInt(trimmed.substring(i, i + 2), 16);
        }
        return data;
    }
}
