package com.podigua.kafka.visark.home.convert.deserialization;

import com.podigua.kafka.visark.home.convert.MessageDeserialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * base64-反序列化
 *
 * @author podigua
 * @date 2025/06/09
 */
public class Base64Deserialization implements MessageDeserialization {
    private final static Logger logger = LoggerFactory.getLogger(StringDeserialization.class);
    private final String charset;

    public Base64Deserialization(String charset) {
        this.charset = charset;
    }

    @Override
    public String deserialize(byte[] bytes) {
        if(bytes==null || bytes.length==0){
            return null;
        }
        try {
            return new String(Base64.getEncoder().encode(bytes),this.charset);
        } catch (UnsupportedEncodingException e) {
            logger.error("消息转换失败", e);
            throw null;
        }
    }
}
