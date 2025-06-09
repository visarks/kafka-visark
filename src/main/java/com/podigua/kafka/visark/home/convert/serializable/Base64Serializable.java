package com.podigua.kafka.visark.home.convert.serializable;

import com.podigua.kafka.visark.home.convert.MessageSerializable;
import com.podigua.kafka.visark.home.convert.deserialization.StringDeserialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * 十六进制可序列化
 *
 * @author podigua
 * @date 2025/06/09
 */
public class Base64Serializable implements MessageSerializable {
    private final static Logger logger = LoggerFactory.getLogger(StringDeserialization.class);
    private final String charset;

    public Base64Serializable(String charset) {
        this.charset = charset;
    }

    @Override
    public byte[] serialize(String message) {
        if (!StringUtils.hasText(message)) {
            return new byte[0];
        }
        try {
            return Base64.getDecoder().decode(message.getBytes(this.charset));
        } catch (UnsupportedEncodingException e) {
            logger.error("消息转换失败", e);
            throw new RuntimeException(e);
        }
    }
}
