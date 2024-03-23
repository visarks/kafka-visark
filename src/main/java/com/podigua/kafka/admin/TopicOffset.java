package com.podigua.kafka.admin;

import org.apache.kafka.common.TopicPartition;

/**
 * 主题偏移
 *
 * @author podigua
 * @date 2024/03/24
 */
public class TopicOffset {
    private final TopicPartition topicPartition;
    private final Long start;
    private final Long end;

    public TopicOffset(TopicPartition topicPartition, Long start, Long end) {
        this.topicPartition = topicPartition;
        this.start = start;
        this.end = end;
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
    public Long start(){
        return start;
    }

    /**
     * 结束
     *
     * @return {@link Long}
     */
    public Long end(){
        return end;
    }

    /**
     * 主题
     *
     * @return {@link String}
     */
    public String topic(){
        return this.topicPartition.topic();
    }

    /**
     * 主题
     *
     * @return {@link String}
     */
    public Integer partition(){
        return this.topicPartition.partition();
    }
}
