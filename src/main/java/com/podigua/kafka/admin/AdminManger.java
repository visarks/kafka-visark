package com.podigua.kafka.admin;

import com.podigua.kafka.core.utils.UUIDUtils;
import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import com.podigua.kafka.visark.setting.SettingClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Admin 管理
 *
 * @author podigua
 * @date 2024/03/21
 */
public class AdminManger {
    private final static Map<String, KafkaAdminClient> CLIENTS = new HashMap<>();
    private final static Map<String, ClusterProperty> PROPERTY = new HashMap<>();


    /**
     * 连接
     *
     * @param property 属性
     * @return {@link KafkaAdminClient}
     */
    public static KafkaAdminClient connect(ClusterProperty property) {
        PROPERTY.put(property.getId(), property);
        Admin admin = new Admin(property);
        KafkaAdminClient client = null;
        try {
            client = (KafkaAdminClient) AdminClient.create(admin.properties());
            DescribeClusterResult result = client.describeCluster(new DescribeClusterOptions().timeoutMs(admin.timeout()));
            KafkaFuture<Node> controller = result.controller();
            controller.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return client;
    }

    /**
     * put
     *
     * @param id     编号
     * @param client 客户
     */
    public static void put(String id, KafkaAdminClient client) {
        CLIENTS.put(id, client);
    }

    /**
     * 获取
     *
     * @param id 编号
     */
    public static KafkaAdminClient get(String id) {
        return CLIENTS.get(id);
    }

    /**
     * 删除
     *
     * @param id 编号
     */
    public static void remove(String id) {
        PROPERTY.remove(id);
        Optional.ofNullable(CLIENTS.get(id)).ifPresent(client -> {
            client.close(Duration.ofSeconds(10));
            CLIENTS.remove(id);
        });
    }

    public static void close(KafkaAdminClient client){
        client.close(Duration.ofSeconds(10));
    }

    /**
     * 翻译 错误信息
     *
     * @param throwable 可投掷
     * @return {@link Throwable}
     */
    public static Throwable translate(Throwable throwable) {
        List<Throwable> list = new ArrayList<>();
        Throwable cause = throwable.getCause();
        while (cause != null) {
            list.add(cause);
            cause = cause.getCause();
        }
        for (Throwable t : list) {
            if (t instanceof TimeoutException) {
                return new RuntimeException(SettingClient.bundle().getString("cluster.connect.timeout"));
            }
        }
        return cause == null ? throwable : cause;
    }

    /**
     * 获取属性
     *
     * @param clusterId 集群 ID
     * @return {@link ClusterProperty}
     */
    public static ClusterProperty property(String clusterId) {
        return PROPERTY.get(clusterId);
    }


    /**
     * 获取主题偏移量
     *
     * @param clusterId 集群 ID
     * @param topic     主题
     * @return {@link List}<{@link TopicOffset}>
     */
    public static List<TopicOffset> getTopicOffsets(String clusterId,String topic) {
        ClusterProperty property = property(clusterId);
        Properties properties = new Admin(property).properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUIDUtils.groupId());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        List<TopicOffset> result = new ArrayList<>();
        try (KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(properties)) {
            List<TopicPartition> tps = Optional.ofNullable(consumer.partitionsFor(topic, Duration.ofSeconds(SettingClient.get().getTimeout())))
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(info -> new TopicPartition(info.topic(), info.partition()))
                    .collect(Collectors.toList());
            Map<TopicPartition, Long> begin = consumer.beginningOffsets(tps);
            Map<TopicPartition, Long> end = consumer.endOffsets(tps);
            begin.forEach((tp, offset) -> {
                result.add(new TopicOffset(tp, offset, end.get(tp)));
            });
        }
        return result;
    }

    /**
     * 获取主题偏移量
     *
     * @param clusterId 集群 ID
     * @param topic     主题
     * @return {@link List}<{@link TopicOffset}>
     */
    public static List<TopicTimeOffset> getTopicOffsetsByTime(String clusterId,String topic,Long timestamp) {
        ClusterProperty property = property(clusterId);
        Properties properties = new Admin(property).properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUIDUtils.groupId());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        List<TopicTimeOffset> result = new ArrayList<>();
        try (KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(properties)) {
            List<TopicPartition> tps = Optional.ofNullable(consumer.partitionsFor(topic, Duration.ofSeconds(SettingClient.get().getTimeout())))
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(info -> new TopicPartition(info.topic(), info.partition()))
                    .collect(Collectors.toList());

            // 使用offsetsForTimes()方法根据时间戳查找offset
            Map<TopicPartition, Long> timestampsToSearch = tps.stream()
                    .collect(Collectors.toMap(Function.identity(), tp -> timestamp));
            Map<TopicPartition, OffsetAndTimestamp> offsetTimestamps = consumer.offsetsForTimes(timestampsToSearch);

            offsetTimestamps.forEach((tp, offset) -> {
                if(offset!=null){
                    result.add(new TopicTimeOffset(tp, offset.timestamp(), offset.offset()));
                }
            });
        }
        return result;
    }
}
