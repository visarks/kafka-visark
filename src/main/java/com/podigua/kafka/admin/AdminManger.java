package com.podigua.kafka.admin;

import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import com.podigua.kafka.visark.setting.SettingClient;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Admin 管理
 *
 * @author podigua
 */

public class AdminManger {
    private static final Logger logger = LoggerFactory.getLogger(AdminManger.class);
    private final static Map<String, KafkaAdminClient> CLIENTS = new HashMap<>();
    private final static Map<String, ClusterProperty> PROPERTY = new HashMap<>();
    // 正在连接中的客户端，用于取消时关闭
    private final static Map<String, KafkaAdminClient> CONNECTING = new HashMap<>();

    /** 连接验证超时（秒） */
    private static final int CONNECT_TIMEOUT_SECONDS = 15;
    /** Socket 连接超时（毫秒）- 网络差时需要更长 */
    private static final int SOCKET_TIMEOUT_MS = 10000;

    /**
     * Socket 验证 bootstrap servers 是否可达
     *
     * @param bootstrapServers bootstrap servers 配置，如 "host1:9092,host2:9092"
     * @return 是否至少有一个地址可达
     */
    public static boolean socketVerify(String bootstrapServers) {
        if (bootstrapServers == null || bootstrapServers.isEmpty()) {
            return false;
        }
        String[] servers = bootstrapServers.split(",");
        for (String server : servers) {
            String[] parts = server.trim().split(":");
            if (parts.length != 2) {
                continue;
            }
            String host = parts[0].trim();
            int port = Integer.parseInt(parts[1].trim());
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), SOCKET_TIMEOUT_MS);
                logger.info("Socket 验证成功: {}:{}", host, port);
                return true;
            } catch (Exception e) {
                logger.error("Socket 验证失败: {}:{}", host, port, e.getMessage());
            }
        }
        return false;
    }


    /**
     * 连接并验证
     *
     * @param property 属性
     * @return {@link KafkaAdminClient}
     */
    public static KafkaAdminClient connect(ClusterProperty property) {
        String clusterId = property.getId();
        PROPERTY.put(clusterId, property);
        Admin admin = new Admin(property);
        KafkaAdminClient client = null;
        try {
            long start = System.currentTimeMillis();

            // 先进行 Socket 验证，快速排除不可达地址
            if (!socketVerify(admin.bootstrapServers())) {
                PROPERTY.remove(clusterId);
                throw new TimeoutException("无法连接到任何 Kafka 服务器: " + admin.bootstrapServers());
            }

            client = (KafkaAdminClient) AdminClient.create(admin.properties());
            // 暂存正在连接的客户端，以便取消时能关闭
            CONNECTING.put(clusterId, client);
            DescribeClusterResult result = client.describeCluster();
            String id = result.clusterId()
                    .toCompletionStage()
                    .toCompletableFuture()
                    .orTimeout(admin.timeout(), TimeUnit.SECONDS)
                    .get();
            logger.info("连接集群成功, clusterId: {}, 耗时: {}ms", id, System.currentTimeMillis() - start);
            return client;
        } catch (Exception e) {
            // 连接失败时关闭客户端并清理
            if (client != null) {
                try {
                    client.close(Duration.ofSeconds(5));
                } catch (Exception ignored) {
                }
            }
            PROPERTY.remove(clusterId);
            throw new RuntimeException(e);
        } finally {
            CONNECTING.remove(clusterId);
        }
    }

    /**
     * 取消正在进行的连接
     *
     * @param clusterId 集群 ID
     */
    public static void cancelConnect(String clusterId) {
        KafkaAdminClient client = CONNECTING.remove(clusterId);
        if (client != null) {
            try {
                client.close(Duration.ofSeconds(1));
                logger.info("已取消连接, clusterId: {}", clusterId);
            } catch (Exception ignored) {
            }
            PROPERTY.remove(clusterId);
        }
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
                    .map(p -> new TopicPartition(topic, p.partition()))
                    .collect(Collectors.toSet());

            Map<TopicPartition, OffsetSpec> latestSpec = topicPartitions.stream()
                    .collect(Collectors.toMap(tp -> tp, _ -> OffsetSpec.latest()));
            Map<TopicPartition, OffsetSpec> earliestSpec = topicPartitions.stream()
                    .collect(Collectors.toMap(tp -> tp, _ -> OffsetSpec.earliest()));

            // 并行查询 earliest 和 latest 偏移量
            CompletableFuture<Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo>> latestFuture =
                    client.listOffsets(latestSpec).all().toCompletionStage().toCompletableFuture();
            CompletableFuture<Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo>> earliestFuture =
                    client.listOffsets(earliestSpec).all().toCompletionStage().toCompletableFuture();

            CompletableFuture.allOf(latestFuture, earliestFuture).join();

            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> latestOffsets = latestFuture.get();
            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> earliestOffsets = earliestFuture.get();

            for (TopicPartition tp : topicPartitions) {
                result.add(new TopicOffset(tp, earliestOffsets.get(tp).offset(), latestOffsets.get(tp).offset()));
            }
        } catch (Exception e) {
            logger.error("获取主题偏移量失败，clusterId: {}, topic: {}", clusterId, topic, e);
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
    public static List<TopicOffset> getTopicOffsetsByTime(String clusterId, String topic, Long timestamp) {
        KafkaAdminClient client = AdminManger.get(clusterId);
        List<TopicOffset> result = new ArrayList<>();
        try {
            TopicDescription topicDesc = client.describeTopics(Collections.singleton(topic))
                    .allTopicNames().get().get(topic);
            Set<TopicPartition> partitions = topicDesc.partitions().stream()
                    .map(p -> new TopicPartition(topic, p.partition()))
                    .collect(Collectors.toSet());

            // 构建三种 OffsetSpec 查询
            Map<TopicPartition, OffsetSpec> timeSpec = partitions.stream()
                    .collect(Collectors.toMap(tp -> tp, tp -> OffsetSpec.forTimestamp(timestamp)));
            Map<TopicPartition, OffsetSpec> earliestSpec = partitions.stream()
                    .collect(Collectors.toMap(tp -> tp, tp -> OffsetSpec.earliest()));
            Map<TopicPartition, OffsetSpec> latestSpec = partitions.stream()
                    .collect(Collectors.toMap(tp -> tp, tp -> OffsetSpec.latest()));

            // 并行查询三种 offsets
            CompletableFuture<Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo>> timeFuture =
                    client.listOffsets(timeSpec).all().toCompletionStage().toCompletableFuture();
            CompletableFuture<Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo>> earliestFuture =
                    client.listOffsets(earliestSpec).all().toCompletionStage().toCompletableFuture();
            CompletableFuture<Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo>> latestFuture =
                    client.listOffsets(latestSpec).all().toCompletionStage().toCompletableFuture();

            CompletableFuture.allOf(timeFuture, earliestFuture, latestFuture).join();

            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> timeResults = timeFuture.get();
            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> earliestResults = earliestFuture.get();
            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> latestResults = latestFuture.get();

            // 按分区整理结果
            for (TopicPartition tp : partitions) {
                long timeOffset = timeResults.get(tp) != null ? timeResults.get(tp).offset() : -1;
                long earliestOffset = earliestResults.get(tp) != null ? earliestResults.get(tp).offset() : -1;
                long latestOffset = latestResults.get(tp) != null ? latestResults.get(tp).offset() : -1;
                result.add(new TopicOffset(
                        tp,
                        timeOffset == -1 ? earliestOffset : timeOffset,
                        latestOffset
                ));
            }
        } catch (Exception e) {
            logger.error("跟时间获取偏移量失败，clusterId: {}, topic: {}", clusterId, topic, e);
        }
        return result;
    }
}
