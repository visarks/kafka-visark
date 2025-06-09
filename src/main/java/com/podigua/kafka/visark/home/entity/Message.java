package com.podigua.kafka.visark.home.entity;

import com.podigua.kafka.visark.home.convert.MessageDeserialization;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息
 *
 * @author podigua
 * @date 2024/03/25
 */
public class Message {
    private final long millis;
    private final MessageDeserialization keyDeserializer;
    private final MessageDeserialization valueDeserializer;

    public Long millis() {
        return this.millis;
    }

    public Message(ConsumerRecord<byte[], byte[]> record, MessageDeserialization keyDeserializer, MessageDeserialization valueDeserializer) {
        this.topic.set(record.topic());
        this.keyDeserializer = keyDeserializer;
        this.valueDeserializer = valueDeserializer;
        this.partition.set(record.partition());
        this.offset.set(record.offset());
        this.millis = record.timestamp();
        this.timestamp.set(LocalDateTime.ofInstant(Instant.ofEpochMilli(record.timestamp()), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        this.key.set(keyDeserializer.deserialize(record.key()));
        this.value.set(valueDeserializer.deserialize(record.value()));
        record.headers().forEach(header -> {
            this.headers.add(new Header(header.key(), new String(header.value())));
        });
    }

    /**
     * 主题
     */
    private final SimpleStringProperty topic = new SimpleStringProperty("");
    /**
     * 分区
     */
    private final SimpleIntegerProperty partition = new SimpleIntegerProperty();
    /**
     * 抵消
     */
    private final SimpleLongProperty offset = new SimpleLongProperty();
    /**
     * 时间戳
     */
    private final SimpleStringProperty timestamp = new SimpleStringProperty("");

    /**
     * key
     */
    private final SimpleStringProperty key = new SimpleStringProperty("");
    /**
     * value
     */
    private final SimpleStringProperty value = new SimpleStringProperty("");

    /**
     * 头
     */
    private final List<Header> headers = new ArrayList<>();

    public SimpleStringProperty topic() {
        return this.topic;
    }

    public SimpleIntegerProperty partition() {
        return this.partition;
    }

    public SimpleLongProperty offset() {
        return this.offset;
    }

    public SimpleStringProperty timestamp() {
        return this.timestamp;
    }

    public SimpleStringProperty key() {
        return this.key;
    }

    public SimpleStringProperty value() {
        return this.value;
    }

    public List<Header> headers() {
        return this.headers;
    }


    /**
     * 排序
     *
     * @return {@link String}
     */
    public String sort() {
        return this.timestamp.get();
    }

    /**
     * 页眉
     *
     * @author podigua
     * @date 2024/03/25
     */
    public record Header(String key, String value) {

        public String key() {
            return key;
        }

        public String value() {
            return value;
        }
    }
}
