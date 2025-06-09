package com.podigua.kafka.visark.home.convert.deserialization;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;
import com.podigua.kafka.core.utils.ProtobufUtils;
import com.podigua.kafka.visark.home.convert.MessageDeserialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Protobuf-反序列化
 *
 * @author podigua
 * @date 2025/06/09
 */
public class ProtobufDeserialization implements MessageDeserialization {
    private final static Logger logger = LoggerFactory.getLogger(StringDeserialization.class);
    private final String charset;
    private final List<Descriptors.Descriptor> descriptors;

    public ProtobufDeserialization(String charset, String path) {
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
    public String deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        for (Descriptors.Descriptor descriptor : descriptors) {
            try {
                DynamicMessage message = DynamicMessage.parseFrom(descriptor, bytes);
                String fullName = message.getDescriptorForType().getFullName();
                String fullName1 = descriptor.getFullName();
                if(fullName1.equals(fullName)){
                    System.out.println("");
                }
                System.out.println( JsonFormat.printer().includingDefaultValueFields().print(message));
                System.out.println(fullName1);
            } catch (Exception e) {
                logger.warn("Failed to deserialize protobuf message", e);
            }
        }
        for (Descriptors.Descriptor descriptor : descriptors) {
            try {
                DynamicMessage message = DynamicMessage.parseFrom(descriptor, bytes);
                return new String(message.toByteArray(), charset);
            } catch (Exception e) {
                logger.warn("Failed to deserialize protobuf message", e);
            }
        }
        try {
            return new String(bytes, this.charset);
        } catch (UnsupportedEncodingException e) {
            logger.warn("Failed to deserialize protobuf message", e);
            return null;
        }
    }
}
