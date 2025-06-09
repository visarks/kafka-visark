package com.podigua.kafka.visark.home.convert.serializable;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.podigua.kafka.core.utils.ProtobufUtils;
import com.podigua.kafka.visark.home.convert.MessageSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class ProtobufSerializable implements MessageSerializable {
    private final static Logger logger = LoggerFactory.getLogger(ProtobufSerializable.class);
    private final String charset;
    private final List<Descriptors.Descriptor> descriptors;

    public ProtobufSerializable(String charset, String path) {
        this.charset = charset;
        if (!StringUtils.hasText(path)) {
            throw new IllegalArgumentException("Protobuf file can not be empty");
        }
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("Protobuf file not found");
        }
        try {
            descriptors = ProtobufUtils.descriptors(file);
        } catch (Exception e) {
            throw new IllegalArgumentException("Protobuf file is not a valid protobuf file", e);
        }
    }

    @Override
    public byte[] serialize(String message) {
        if (!StringUtils.hasText(message)) {
            return new byte[0];
        }
        try {
            for (Descriptors.Descriptor descriptor : descriptors) {
                try {
                    DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
                    builder.mergeFrom(message.getBytes(this.charset));
                    return builder.build().toByteArray();
                } catch (Exception var4) {
                    logger.warn("Protobuf serialize error", var4);
                }
            }
            return message.getBytes(this.charset);
        } catch (UnsupportedEncodingException e) {
            logger.error("消息转换失败", e);
            throw new RuntimeException(e);
        }
    }
}
