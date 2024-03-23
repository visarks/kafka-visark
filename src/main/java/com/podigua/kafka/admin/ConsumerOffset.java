package com.podigua.kafka.admin;

import org.apache.kafka.common.TopicPartition;

/**
 * 消费者偏移量
 *
 * @author podigua
 * @date 2024/03/24
 */
public class ConsumerOffset {
    /**
     * 主题分区
     */
    private final TopicPartition topicPartition;
    /**
     * 开始
     */
    private final Long start;
    /**
     * 结束
     */
    private final Long end;
    /**
     * 抵消
     */
    private final Long offset;

    public ConsumerOffset(TopicPartition topicPartition, Long start, Long end, Long offset) {
        this.topicPartition = topicPartition;
        this.start = start;
        this.end = end;
        this.offset = offset;
    }

    /**
     * 主题分区
     *
     * @return {@link TopicPartition}
     */
    public TopicPartition topicPartition() {
        return topicPartition;
    }

    /**
     * 开始
     *
     * @return {@link Long}
     */
    public Long start() {
        return start;
    }

    /**
     * 结束
     *
     * @return {@link Long}
     */
    public Long end() {
        return end;
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

    /**
     * 抵消
     *
     * @return {@link Long}
     */
    public Long offset() {
        return this.offset;
    }
}
