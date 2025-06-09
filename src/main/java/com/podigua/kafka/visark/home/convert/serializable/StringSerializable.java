package com.podigua.kafka.visark.home.convert.serializable;

import com.podigua.kafka.visark.home.convert.MessageSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;

public class StringSerializable implements MessageSerializable {
    private final static Logger logger = LoggerFactory.getLogger(StringSerializable.class);
    private final String charset;

    public StringSerializable(String charset) {
        this.charset = charset;
    }

    @Override
    public byte[] serialize(String message) {
        if (!StringUtils.hasText(message)) {
            return new byte[0];
        }
        try {
            return message.getBytes(this.charset);
        } catch (UnsupportedEncodingException e) {
            logger.error("消息转换失败",e);
            throw new RuntimeException(e);
        }
    }
}
