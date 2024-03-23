package com.podigua.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 *
 **/
public class Test3 {
    public static void main(String[] args) {
        // 配置消费者属性
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1"); // 注意这个groupId不影响实际消费者组，仅为连接Kafka用
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 设置偏移量自动重置策略，以便查询当前offset
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 关闭自动提交offset，因为我们只读取offset不消费消息

        // 创建消费者实例
        Consumer<String, String> consumer = new KafkaConsumer<>(props, new StringDeserializer(), new StringDeserializer());

        // 想要查询的Topic列表
        String[] topics = {"test"};

        // 订阅主题，并获取每个分区的当前位移
        consumer.subscribe(Arrays.asList(topics));
        Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(consumer.assignment());
        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(consumer.assignment());

        // 计算每个分区的消息积压数量
        for (TopicPartition tp : beginningOffsets.keySet()) {
            long currentOffset = endOffsets.get(tp);
            long startOffset = beginningOffsets.get(tp);
            long pendingMessages = currentOffset - startOffset;
            System.out.println("For TopicPartition " + tp + ", pending messages: " + pendingMessages);
        }

        // 关闭消费者
        consumer.close();
    }
}
