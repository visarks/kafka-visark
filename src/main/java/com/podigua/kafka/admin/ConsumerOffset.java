package com.podigua.kafka.admin;

import org.apache.kafka.common.TopicPartition;

/**
 * 消费者偏移量
 *
 * @param topicPartition 主题分区
 * @param start          开始
 * @param end            结束
 * @param offset         抵消
 * @author podigua
 * @date 2024/03/24
 */
public record ConsumerOffset(TopicPartition topicPartition, String host, String memberId, String clientId, Long start,
                             Long end, Long offset) {

    /**
     * 主机
     *
     * @return {@link String}
     */
    public String host() {
        return host;
    }

    /**
     * 会员 ID
     *
     * @return {@link String}
     */
    public String memberId() {
        return memberId;
    }

    /**
     * 客户端 ID
     *
     * @return {@link String}
     */
    public String clientId() {
        return clientId;
    }

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
     * 开始
     *
     * @return {@link Long}
     */
    @Override
    public Long start() {
        return start;
    }

    /**
     * 结束
     *
     * @return {@link Long}
     */
    @Override
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
    @Override
    public Long offset() {
        return this.offset;
    }
}
