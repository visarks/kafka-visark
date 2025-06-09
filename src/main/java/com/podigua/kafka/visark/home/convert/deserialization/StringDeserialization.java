package com.podigua.kafka.visark.home.convert.deserialization;

import com.podigua.kafka.visark.home.convert.MessageDeserialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字符串消息转换
 *
 * @author podigua
 * @date 2025/06/09
 */
public class StringDeserialization implements MessageDeserialization {
    private final static Logger logger = LoggerFactory.getLogger(StringDeserialization.class);
    private final String charset;

    public StringDeserialization(String charset) {
        this.charset = charset;
    }

    @Override
    public String deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return new String(bytes, this.charset);
        } catch (Exception e) {
            logger.error("消息转换失败", e);
            throw null;
        }
    }
}
