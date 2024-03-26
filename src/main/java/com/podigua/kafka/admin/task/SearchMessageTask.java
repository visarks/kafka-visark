package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.*;
import com.podigua.kafka.admin.enums.OffsetType;
import com.podigua.kafka.admin.enums.SearchType;
import com.podigua.kafka.core.utils.UUIDUtils;
import com.podigua.kafka.event.TooltipEvent;
import com.podigua.kafka.visark.setting.SettingClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
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
    private final AtomicLong counts = new AtomicLong(0);

    /**
     * 主题
     */
    private final String topic;

    private final QueryParams params;
    private final Consumer<ConsumerRecord<byte[], byte[]>> callback;


    public SearchMessageTask(String clusterId, String topic, QueryParams params, Consumer<ConsumerRecord<byte[], byte[]>> callback) {
        super(clusterId);
        this.topic = topic;
        this.params = params;
        this.callback = callback;
    }

    @Override
    protected Long call() throws Exception {
        List<TopicOffset> list = AdminManger.getTopicOffsets(clusterId(), topic);
        if (CollectionUtils.isEmpty(list)) {
            return 0L;
        }
        if (params.partition() != -1) {
            list = list.stream().filter(e -> e.partition() == params.partition()).collect(Collectors.toList());
        }
        Properties properties = new Admin(property()).properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUIDUtils.groupId());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        List<TopicTimeOffset> timeOffset = new ArrayList<>();
        if (SearchType.datetime.equals(params.searchType())) {
            timeOffset = AdminManger.getTopicOffsetsByTime(clusterId(), topic, Timestamp.valueOf(params.time()).getTime());
        }
        if (params.partition() != -1) {
            timeOffset = timeOffset.stream().filter(e -> e.partition() == params.partition()).collect(Collectors.toList());
        }
        long start = System.currentTimeMillis();
        long lastSendTime = System.currentTimeMillis();
        List<ConsumerRecord<byte[], byte[]>> result = new ArrayList<>();
        try (KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(properties)) {
            List<Offset> offsets = compOffset(list, timeOffset);
            if (CollectionUtils.isEmpty(offsets)) {
                return 0L;
            }
            consumer.assign(offsets.stream().map(Offset::topicPartition).collect(Collectors.toList()));
            for (Offset offset : offsets) {
                consumer.seek(offset.topicPartition, offset.start);
                exit:
                while (true) {
                    ConsumerRecords<byte[], byte[]> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<byte[], byte[]> record : records) {
                        counts.getAndIncrement();
                        result.add(record);
                        callback.accept(record);
                        if (System.currentTimeMillis() - lastSendTime > 5000) {
                            lastSendTime = System.currentTimeMillis();
                            TooltipEvent.info(String.format(SettingClient.bundle().getString("message.search.tooltip"), counts.get(), (System.currentTimeMillis() - start))).publishAsync();
                        }
                        if (record.offset() >= offset.end) {
                            break exit;
                        }
                    }
                }
            }
        }
        return counts.get();
    }

    private List<Offset> compOffset(List<TopicOffset> list, List<TopicTimeOffset> times) {
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

    private List<Offset> byTime(List<TopicOffset> topicOffsets, List<TopicTimeOffset> times) {
        List<Offset> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(times)) {
            return result;
        }
        for (TopicTimeOffset time : times) {
            TopicOffset topicOffset = topicOffsets.stream().filter(topicoffset -> time.partition() == topicoffset.partition()).findFirst().orElse(null);
            if (topicOffset == null) {
                continue;
            }
            if (OffsetType.earliest.equals(params.offsetType())) {
                if (time.offset() >= topicOffset.start() && time.offset() <= topicOffset.end()) {
                    var start = Math.max(topicOffset.start(), (time.offset() - params.count()));
                    if (time.offset() > start) {
                        result.add(new Offset(topicOffset.topicPartition(), start, time.offset()));
                    }
                }

            } else {
                if (time.offset() >= topicOffset.start() && time.offset() <= topicOffset.end()) {
                    long end = Math.min(topicOffset.end(), time.offset() + params.count());
                    if (end > time.offset()) {
                        result.add(new Offset(topicOffset.topicPartition(), time.offset(), +end - 1));
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
