package com.podigua.kafka.visark.home.convert.deserialization;

import com.podigua.kafka.visark.home.convert.MessageDeserialization;

import java.util.HexFormat;

/**
 * 十六进制-反序列化
 *
 * @author podigua
 * @date 2025/06/09
 */
public class HexDeserialization implements MessageDeserialization {
    public static HexDeserialization DEFAULT = new HexDeserialization();

    @Override
    public String deserialize(byte[] bytes) {
        if(bytes==null || bytes.length==0){
            return null;
        }
        return HexFormat.ofDelimiter(" ").formatHex(bytes);
    }
}
