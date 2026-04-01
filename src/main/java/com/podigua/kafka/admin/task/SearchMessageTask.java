package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.*;
import com.podigua.kafka.admin.enums.OffsetType;
import com.podigua.kafka.admin.enums.SearchType;
import com.podigua.kafka.core.utils.ThreadUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 搜索消息任务
 *
 * @author podigua
 */
public class SearchMessageTask extends QueryTask<Long> {
    private static final Logger logger = LoggerFactory.getLogger(SearchMessageTask.class);
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final AtomicLong counts = new AtomicLong(0);
    /** poll 超时时间（毫秒） */
    private static final int POLL_TIMEOUT_MS = 100;
    /** 批量回调大小 */
    private static final int BATCH_SIZE = 100;
    /** 每个 Consumer 最大处理分区数 */
    private static final int MAX_PARTITIONS_PER_CONSUMER = 5;
    /**
     * 主题
     */
    private final String topic;

    private final QueryParams params;
    private final Consumer<ConsumerRecord<byte[], byte[]>> callback;

    /**
     * 关闭
     */
    public void shutdown() {
        shutdown.set(true);
    }

    /**
     * 是关闭
     *
     * @return boolean
     */
    public boolean isShutdown() {
        return shutdown.get();
    }

    public SearchMessageTask(String clusterId, String topic, QueryParams params, Consumer<ConsumerRecord<byte[], byte[]>> callback) {
        super(clusterId);
        this.topic = topic;
        this.params = params;
        this.callback = callback;
    }

    @Override
    protected Long call() throws Exception {
        long start = System.currentTimeMillis();
        logger.info("总任务查询开始:{}", topic);
        List<TopicOffset> list = AdminManger.getTopicOffsets(clusterId(), topic);
        if (CollectionUtils.isEmpty(list)) {
            return 0L;
        }
        Properties properties = new Admin(property()).properties();
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        List<TopicOffset> timeOffset = new ArrayList<>();
        if (SearchType.datetime.equals(params.searchType())) {
            timeOffset = AdminManger.getTopicOffsetsByTime(clusterId(), topic, Timestamp.valueOf(params.time()).getTime());
        }
        List<Offset> offsets = compOffset(list, timeOffset);
        if (CollectionUtils.isEmpty(offsets)) {
            return 0L;
        }
        asyncProcess(properties, offsets);
//        syncProcess(properties, offsets);
        logger.info("总任务查询完成,topic:{},总数:{},耗时:{}", topic, counts.get(), (System.currentTimeMillis() - start));
        return counts.get();
    }

    private void asyncProcess(Properties properties, List<Offset> offsets) {
        // 过滤分区
        offsets.removeIf(offset -> !params.partitions().contains(offset.topicPartition.partition()));

        // 将 offsets 分组，每组最多 MAX_PARTITIONS_PER_CONSUMER 个分区
        List<List<Offset>> groups = partitionOffsets(offsets, MAX_PARTITIONS_PER_CONSUMER);
        List<Future<?>> futures = new ArrayList<>();

        for (List<Offset> group : groups) {
            futures.add(ThreadUtils.virtual().submit(() -> {
                try (KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(properties)) {
                    // assign 多个分区
                    List<TopicPartition> partitions = group.stream()
                            .map(Offset::topicPartition)
                            .toList();
                    consumer.assign(partitions);

                    long childStart = System.currentTimeMillis();
                    logger.info("子任务开始查询, partitions: {}", partitions.size());

                    // seek 每个分区
                    Map<TopicPartition, Long> endOffsets = new HashMap<>();
                    for (Offset offset : group) {
                        consumer.seek(offset.topicPartition, offset.start);
                        endOffsets.put(offset.topicPartition, offset.end);
                    }

                    // 批量回调缓冲
                    List<ConsumerRecord<byte[], byte[]>> batch = new ArrayList<>(BATCH_SIZE);

                    exit:
                    while (!shutdown.get()) {
                        ConsumerRecords<byte[], byte[]> records = consumer.poll(Duration.ofMillis(POLL_TIMEOUT_MS));
                        for (ConsumerRecord<byte[], byte[]> record : records) {
                            counts.getAndIncrement();
                            batch.add(record);
                            // 批量回调
                            if (batch.size() >= BATCH_SIZE) {
                                batch.forEach(callback);
                                batch.clear();
                            }
                            // 检查是否到达结束偏移
                            TopicPartition tp = new TopicPartition(record.topic(), record.partition());
                            Long endOffset = endOffsets.get(tp);
                            if (endOffset != null && record.offset() >= endOffset) {
                                endOffsets.remove(tp);
                                if (endOffsets.isEmpty()) {
                                    break exit;
                                }
                            }
                        }
                    }
                    // 处理剩余消息
                    if (!batch.isEmpty()) {
                        batch.forEach(callback);
                    }
                    logger.info("子任务查询结束, partitions: {}, 耗时: {}ms", partitions.size(), System.currentTimeMillis() - childStart);
                } catch (Exception e) {
                    logger.error("接收消息失败", e);
                }
            }));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                logger.error("获取结果失败", e);
            }
        }
    }

    /**
     * 将 offsets 分组
     */
    private List<List<Offset>> partitionOffsets(List<Offset> offsets, int size) {
        List<List<Offset>> groups = new ArrayList<>();
        for (int i = 0; i < offsets.size(); i += size) {
            groups.add(offsets.subList(i, Math.min(i + size, offsets.size())));
        }
        return groups;
    }

    private List<Offset> compOffset(List<TopicOffset> list, List<TopicOffset> times) {
        //按照数量 且从头开始消费
        if (SearchType.messages.equals(params.searchType())) {
            return byMessages(list);
        } else if (SearchType.offset.equals(params.searchType())) {
            return byOffset(list);
        } else if (SearchType.datetime.equals(params.searchType())) {
            return byTime(list, times);
        }
        return new ArrayList<>();
    }

    private List<Offset> byTime(List<TopicOffset> topicOffsets, List<TopicOffset> times) {
        List<Offset> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(times)) {
            return result;
        }

        // 使用 Map 直接查找，避免每次 stream.filter
        Map<Integer, TopicOffset> offsetMap = topicOffsets.stream()
                .collect(Collectors.toMap(TopicOffset::partition, to -> to));

        for (TopicOffset time : times) {
            TopicOffset topicOffset = offsetMap.get(time.partition());
            if (topicOffset == null) {
                continue;
            }
            if (OffsetType.earliest.equals(params.offsetType())) {
                if (time.start() >= topicOffset.start() && time.start() <= topicOffset.end()) {
                    long start = Math.max(topicOffset.start(), time.start() - params.count());
                    if (time.start() > start) {
                        result.add(new Offset(topicOffset.topicPartition(), start, time.start()));
                    }
                }
            } else {
                if (time.end() >= topicOffset.start() && time.end() <= topicOffset.end()) {
                    long end = Math.min(topicOffset.end(), time.start() + params.count());
                    if (end > time.start()) {
                        result.add(new Offset(topicOffset.topicPartition(), time.start(), end - 1));
                    }
                }
            }
        }
        return result;
    }

    private List<Offset> byOffset(List<TopicOffset> list) {
        List<Offset> result = new ArrayList<>();
        for (TopicOffset topicOffset : list) {
            if (Objects.equals(topicOffset.start(), topicOffset.end())) {
                continue;
            }
            if (OffsetType.earliest.equals(params.offsetType())) {
                if (params.offset() >= topicOffset.start() && params.offset() <= topicOffset.end()) {
                    long start = Math.max(topicOffset.start(), params.offset() - params.count());
                    if (params.offset() > start) {
                        result.add(new Offset(topicOffset.topicPartition(), start, params.offset() - 1));
                    }
                }
            } else {
                if (params.offset() >= topicOffset.start() && params.offset() <= topicOffset.end()) {
                    long end = Math.min(topicOffset.end(), params.offset() + params.count());
                    if (end > params.offset()) {
                        result.add(new Offset(topicOffset.topicPartition(), params.offset(), end - 1));
                    }
                }
            }
        }
        return result;
    }

    private List<Offset> byMessages(List<TopicOffset> list) {
        List<Offset> result = new ArrayList<>();
        for (TopicOffset topicOffset : list) {
            if (Objects.equals(topicOffset.start(), topicOffset.end())) {
                continue;
            }
            if (OffsetType.earliest.equals(params.offsetType())) {
                long end = Math.min(topicOffset.end(), topicOffset.start() + params.count());
                result.add(new Offset(topicOffset.topicPartition(), topicOffset.start(), end - 1));
            } else {
                long start = Math.max(topicOffset.start(), topicOffset.end() - params.count());
                result.add(new Offset(topicOffset.topicPartition(), start, topicOffset.end() - 1));

            }
        }
        return result;
    }

    private record Offset(TopicPartition topicPartition, Long start, Long end) {

    }
}
