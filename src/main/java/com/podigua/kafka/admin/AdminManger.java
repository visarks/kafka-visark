package com.podigua.kafka.admin;

import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import com.podigua.kafka.visark.setting.SettingClient;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Admin 管理
 *
 * @author podigua
 * @date 2024/03/21
 */
public class AdminManger {
    private static final Logger logger = LoggerFactory.getLogger(AdminManger.class);
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

    public static void close(KafkaAdminClient client) {
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
    public static List<TopicOffset> getTopicOffsets(String clusterId, String topic) {
        List<TopicOffset> result = new ArrayList<>();
        KafkaAdminClient client = AdminManger.get(clusterId);

        try {
            TopicDescription topicDescription = client.describeTopics(Collections.singleton(topic))
                    .allTopicNames().get().get(topic);
            Set<TopicPartition> topicPartitions = topicDescription.partitions().stream()
                    .map(partitionInfo -> new TopicPartition(topic, partitionInfo.partition()))
                    .collect(Collectors.toSet());
            Map<TopicPartition, OffsetSpec> offsetMap = topicPartitions.stream()
                    .collect(Collectors.toMap(
                            tp -> tp,
                            tp -> OffsetSpec.latest()
                    ));
            Map<TopicPartition, OffsetSpec> earliestOffsetMap = topicPartitions.stream()
                    .collect(Collectors.toMap(
                            tp -> tp,
                            tp -> OffsetSpec.earliest()
                    ));
            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> latestOffsets =
                    client.listOffsets(offsetMap).all().get();
            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> earliestOffsets =
                    client.listOffsets(earliestOffsetMap).all().get();
            for (TopicPartition tp : topicPartitions) {
                long earliestOffset = earliestOffsets.get(tp).offset();
                long latestOffset = latestOffsets.get(tp).offset();
                result.add(new TopicOffset(tp, earliestOffset, latestOffset));
            }
            return result;

        } catch (Exception e) {
            logger.error("获取主题偏移量失败，clusterId: {}, topic: {}", clusterId, topic, e);
            return result;
        }
    }

    /**
     * 获取主题偏移量
     *
     * @param clusterId 集群 ID
     * @param topic     主题
     * @return {@link List}<{@link TopicOffset}>
     */
    public static List<TopicOffset> getTopicOffsetsByTime(String clusterId, String topic, Long timestamp) {
        KafkaAdminClient client = AdminManger.get(clusterId);
        List<TopicOffset> result = new ArrayList<>();
        try {
            TopicDescription topicDesc = client.describeTopics(Collections.singleton(topic))
                    .allTopicNames().get(10, TimeUnit.SECONDS).get(topic);
            Set<TopicPartition> partitions = topicDesc.partitions().stream()
                    .map(p -> new TopicPartition(topic, p.partition()))
                    .collect(Collectors.toSet());

            // 2. 构建三种 OffsetSpec 查询
            Map<TopicPartition, OffsetSpec> timeOffSpec = new HashMap<>();
            Map<TopicPartition, OffsetSpec> earliestOffSpec = new HashMap<>();
            Map<TopicPartition, OffsetSpec> latestOffSpec = new HashMap<>();
            for (TopicPartition tp : partitions) {
                timeOffSpec.put(tp, OffsetSpec.forTimestamp(timestamp));
                earliestOffSpec.put(tp, OffsetSpec.earliest());
                latestOffSpec.put(tp, OffsetSpec.latest());
            }

            // 3. 批量查询 offsets
            ListOffsetsResult timeResult = client.listOffsets(timeOffSpec);
            ListOffsetsResult earliestResult = client.listOffsets(earliestOffSpec);
            ListOffsetsResult latestResult = client.listOffsets(latestOffSpec);
            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> timeResults = timeResult.all().get();
            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> earliestResults = earliestResult.all().get();
            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> latestResults = latestResult.all().get();

            // 4. 按分区整理结果
            for (TopicPartition tp : partitions) {
                ListOffsetsResult.ListOffsetsResultInfo timeInfo = timeResults.get(tp);
                long timeOffset = timeInfo != null ? timeInfo.offset() : -1;
                ListOffsetsResult.ListOffsetsResultInfo earliestInfo = earliestResults.get(tp);
                long earliestOffset = earliestInfo != null ? earliestInfo.offset() : -1;
                ListOffsetsResult.ListOffsetsResultInfo latestInfo = latestResults.get(tp);
                long latestOffset = latestInfo != null ? latestInfo.offset() : -1;
                result.add(new TopicOffset(
                        tp,
                        timeOffset == -1 ? earliestOffset : timeOffset,
                        latestOffset
                ));
            }
        } catch (Exception e) {
            logger.error("跟时间获取偏移量失败，clusterId: {}, topic: {}", clusterId, topic, e);
            return result;
        }
        return result;
    }
}
