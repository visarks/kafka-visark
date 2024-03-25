package com.podigua.kafka.visark.home.entity;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息
 *
 * @author podigua
 * @date 2024/03/25
 */
public class Message {
    public Message(ConsumerRecord<byte[], byte[]> record) {
        this.topic = record.topic();
        this.partition = record.partition();
        this.offset = record.offset();
        this.timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(record.timestamp()), ZoneId.systemDefault());
        this.key = new String(record.key());
        this.value = new String(record.value());
        record.headers().forEach(header -> {
            this.headers.add(new Header(header.key(), new String(header.value())));
        });
    }

    /**
     * 主题
     */
    private String topic;
    /**
     * 分区
     */
    private Integer partition;
    /**
     * 抵消
     */
    private Long offset;
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    /**
     * key
     */
    private String key;
    /**
     * value
     */
    private String value;

    /**
     * 头
     */
    private List<Header> headers = new ArrayList<>();

    public String topic() {
        return this.topic;
    }

    public Integer partition() {
        return this.partition;
    }

    public Long offset() {
        return this.offset;
    }

    public LocalDateTime timestamp() {
        return this.timestamp;
    }

    public String key() {
        return this.key;
    }

    public String value() {
        return this.value;
    }

    public List<Header> headers() {
        return this.headers;
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
