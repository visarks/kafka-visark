package com.podigua.kafka.visark.home.convert;

import com.podigua.kafka.visark.home.convert.deserialization.Base64Deserialization;
import com.podigua.kafka.visark.home.convert.deserialization.HexDeserialization;
import com.podigua.kafka.visark.home.convert.deserialization.ProtobufDeserialization;
import com.podigua.kafka.visark.home.convert.deserialization.StringDeserialization;
import com.podigua.kafka.visark.home.convert.serializable.Base64Serializable;
import com.podigua.kafka.visark.home.convert.serializable.HexSerializable;
import com.podigua.kafka.visark.home.convert.serializable.ProtobufSerializable;
import com.podigua.kafka.visark.home.convert.serializable.StringSerializable;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息转换工厂
 *
 * @author podigua
 * @date 2025/06/09
 */
public class MessageConvertFactory {
    public static String HEX_TYPE = "Hex";
    public static String BASE64_TYPE = "Base64";
    public static String STRING_TYPE = "String";
    public static String PROTOBUF_TYPE = "Protobuf";


    private static final Map<String, StringSerializable> STRING_SERIALIZABLE = new ConcurrentHashMap<>();
    private static final Map<String, StringDeserialization> STRING_DESERIALIZABLE = new ConcurrentHashMap<>();
    private static final Map<String, Base64Serializable> BASE64_SERIALIZABLE = new ConcurrentHashMap<>();
    private static final Map<String, Base64Deserialization> BASE64_DESERIALIZABLE = new ConcurrentHashMap<>();

    /**
     * 类型
     *
     * @return {@link String[] }
     */
    public static String[] types() {
        return new String[]{STRING_TYPE, HEX_TYPE, BASE64_TYPE};
    }

    public static boolean isShowCharset(String type) {
        if (STRING_TYPE.equals(type) || BASE64_TYPE.equals(type) || PROTOBUF_TYPE.equals(type)) {
            return true;
        }
        return false;
    }

    public static boolean isShowProtobuf(String type) {
        if (PROTOBUF_TYPE.equals(type)) {
            return true;
        }
        return false;
    }


    /**
     * 字符集
     *
     * @return {@link String[] }
     */
    public static String[] charsets() {
        return Charset.availableCharsets().keySet().toArray(new String[0]);
    }

    public static MessageSerializable serializable(String type, String charset, String protobuf) {
        if (HEX_TYPE.equals(type)) {
            return HexSerializable.DEFAULT;
        }
        if (BASE64_TYPE.equals(type)) {
            return BASE64_SERIALIZABLE.computeIfAbsent(charset, k -> new Base64Serializable(charset));
        }
        if (STRING_TYPE.equals(type)) {
            return STRING_SERIALIZABLE.computeIfAbsent(charset, k -> new StringSerializable(charset));
        }
        if (PROTOBUF_TYPE.equals(type)) {
            return new ProtobufSerializable(charset, protobuf);
        }
        return null;
    }

    public static MessageDeserialization deserialization(String type, String charset,String protobuf) {
        if (HEX_TYPE.equals(type)) {
            return HexDeserialization.DEFAULT;
        }
        if (BASE64_TYPE.equals(type)) {
            return BASE64_DESERIALIZABLE.computeIfAbsent(charset, k -> new Base64Deserialization(charset));
        }
        if (STRING_TYPE.equals(type)) {
            return STRING_DESERIALIZABLE.computeIfAbsent(charset, k -> new StringDeserialization(charset));
        }

        if (PROTOBUF_TYPE.equals(type)) {
            return new ProtobufDeserialization(charset, protobuf);
        }
        return null;
    }
}
