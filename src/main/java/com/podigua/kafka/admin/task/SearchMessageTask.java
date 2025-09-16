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
 * @date 2024/03/25
 */
public class SearchMessageTask extends QueryTask<Long> {
    private static final Logger logger = LoggerFactory.getLogger(SearchMessageTask.class);
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final AtomicLong counts = new AtomicLong(0);
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
        Iterator<Offset> iterator = offsets.iterator();
        while (iterator.hasNext()) {
            Offset next = iterator.next();
            if(!params.partitions().contains(next.topicPartition.partition())){
                iterator.remove();
            }
        }
        List<Future<?>> futures = new ArrayList<>();

        for (Offset offset : offsets) {
            futures.add(ThreadUtils.virtual().submit(() -> {
                try (KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(properties)) {
                    consumer.assign(Collections.singletonList(offset.topicPartition));
                    long childStart = System.currentTimeMillis();
                    logger.info("子任务开始查询,topic:{},partition:{},start:{},end:{}", offset.topicPartition.topic(), offset.topicPartition.partition(), offset.start(), offset.end());
                    consumer.seek(offset.topicPartition, offset.start);
                    exit:
                    while (true) {
                        ConsumerRecords<byte[], byte[]> records = consumer.poll(Duration.ofMillis(10));
                        for (ConsumerRecord<byte[], byte[]> record : records) {
                            counts.getAndIncrement();
                            if (shutdown.get()) {
                                break exit;
                            }
                            callback.accept(record);
                            if (record.offset() >= offset.end) {
                                break exit;
                            }
                        }
                    }
                    logger.info("子任务查询结束,topic{},partition:{},耗时:{}", offset.topicPartition.topic(), offset.topicPartition.partition(), (System.currentTimeMillis() - childStart));
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

    private void syncProcess(Properties properties, List<Offset> offsets) {
        try (KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(properties)) {
            consumer.assign(offsets.stream().map(Offset::topicPartition).collect(Collectors.toList()));
            for (Offset offset : offsets) {
                long childStart = System.currentTimeMillis();
                logger.info("子任务开始查询,topic:{},partition:{},start:{},end:{}", offset.topicPartition.topic(), offset.topicPartition.partition(), offset.start(), offset.end());
                consumer.seek(offset.topicPartition, offset.start);
                exit:
                while (true) {
                    ConsumerRecords<byte[], byte[]> records = consumer.poll(Duration.ofMillis(10));
                    for (ConsumerRecord<byte[], byte[]> record : records) {
                        counts.getAndIncrement();
                        if (shutdown.get()) {
                            break exit;
                        }
                        callback.accept(record);
                        if (record.offset() >= offset.end) {
                            break exit;
                        }
                    }
                }
                logger.info("子任务查询结束,topic{},partition:{},耗时:{}", offset.topicPartition.topic(), offset.topicPartition.partition(), (System.currentTimeMillis() - childStart));
            }
        }
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

        for (TopicOffset time : times) {
            TopicOffset topicOffset = topicOffsets.stream().filter(topicoffset -> time.partition() == topicoffset.partition()).findFirst().orElse(null);
            if (topicOffset == null) {
                continue;
            }
            if (OffsetType.earliest.equals(params.offsetType())) {
                if (time.start() >= topicOffset.start() && time.start() <= topicOffset.end()) {
                    var start = Math.max(topicOffset.start(), (time.start() - params.count()));
                    if (time.start() > start) {
                        result.add(new Offset(topicOffset.topicPartition(), start, time.start()));
                    }
                }

            } else {
                if (time.end() >= topicOffset.start() && time.end() <= topicOffset.end()) {
                    long end = Math.min(topicOffset.end(), time.start() + params.count());
                    if (end > time.start()) {
                        result.add(new Offset(topicOffset.topicPartition(), time.start(), +end - 1));
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
                    var start = Math.max(topicOffset.start(), (params.offset() - params.count()));
                    if (params.offset() > start) {
                        result.add(new Offset(topicOffset.topicPartition(), start, params.offset() - 1));
                    }
                }
            } else {
                if (params.offset() >= topicOffset.start() && params.offset() <= topicOffset.end()) {
                    long end = Math.min(topicOffset.end(), params.offset() + params.count());
                    if (end > params.offset()) {
                        result.add(new Offset(topicOffset.topicPartition(), params.offset(), +end - 1));
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
