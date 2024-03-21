package com.podigua.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 *
 **/
public class Test2 {
    public static void main(String[] args) {
        // 配置Kafka消费者的属性
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        // 消费主题
        String topic="test";
        // Create AdminClient
        try (AdminClient adminClient = AdminClient.create(props)) {
            // 获取消费者列表
            Collection<ConsumerGroupListing> consumerGroupListing = adminClient.listConsumerGroups().all().get();
            for (ConsumerGroupListing consumerGroup : consumerGroupListing) {
                String groupId = consumerGroup.groupId();
                // 获取消费者在每个分区的消费偏移
                ListConsumerGroupOffsetsResult offsetsResult = adminClient.listConsumerGroupOffsets(groupId);
                Map<TopicPartition, OffsetAndMetadata> offsets = offsetsResult.partitionsToOffsetAndMetadata().get();
                // 输出指定主题的消费偏移
                for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : offsets.entrySet()) {
                    TopicPartition topicPartition = entry.getKey();
                    if(topic.equals(topicPartition.topic())) {
                        OffsetAndMetadata offsetAndMetadata = entry.getValue();
                        long offset = offsetAndMetadata.offset();
                        System.out.println("Consumer group: " + groupId + ", Topic: " + topicPartition.topic() +
                                ", Partition: " + topicPartition.partition() + ", Offset: " + offset);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
