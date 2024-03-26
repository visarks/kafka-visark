package com.podigua.kafka.admin;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * 消息记录
 *
 * @author podigua
 * @date 2024/03/26
 */
public record MessageRecord(ConsumerRecord<byte[], byte[]> record, Long count, Long millis, boolean done) {


    public ConsumerRecord<byte[], byte[]> record() {
        return record;
    }

    /**
     * 计数
     *
     * @return {@link Long}
     */
    public Long count() {
        return count;
    }

    /**
     * 毫秒
     *
     * @return {@link Long}
     */
    public Long millis() {
        return millis;
    }

    public boolean done() {
        return done;
    }

}
