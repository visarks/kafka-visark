package com.podigua.kafka.admin;

import org.apache.kafka.common.TopicPartition;

/**
 * 主题偏移
 *
 * @author podigua
 * @date 2024/03/24
 */
public record TopicTimeOffset(TopicPartition topicPartition, Long timestamp, Long offset) {

    /**
     * 主题分区
     *
     * @return {@link TopicPartition}
     */
    @Override
    public TopicPartition topicPartition() {
        return topicPartition;
    }

    /**
     * 时间
     *
     * @return {@link Long}
     */
    @Override
    public Long timestamp() {
        return timestamp;
    }

    /**
     * 偏移
     *
     * @return {@link Long}
     */
    @Override
    public Long offset() {
        return offset;
    }

    /**
     * 主题
     *
     * @return {@link String}
     */
    public String topic() {
        return this.topicPartition.topic();
    }

    /**
     * 主题
     *
     * @return {@link String}
     */
    public Integer partition() {
        return this.topicPartition.partition();
    }
}
