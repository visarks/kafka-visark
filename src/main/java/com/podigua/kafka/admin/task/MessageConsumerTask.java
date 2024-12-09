package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.Admin;
import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.core.utils.Lists;
import com.podigua.kafka.core.utils.UUIDUtils;
import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * 搜索消息任务
 *
 * @author podigua
 * @date 2024/03/25
 */
public class MessageConsumerTask extends QueryTask<Long> {

    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    /**
     * 计数
     */
    private final AtomicLong counts = new AtomicLong(0);
    /**
     * 主题
     */
    private final String topic;

    /**
     * 抵消
     */
    private final String offset;
    /**
     * 回调
     */
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

    public MessageConsumerTask(String clusterId, String topic, String offset, Consumer<ConsumerRecord<byte[], byte[]>> callback) {
        super(clusterId);
        this.topic = topic;
        this.offset = offset;
        this.callback = callback;
    }


    @Override
    protected Long call() throws Exception {
        ClusterProperty property = AdminManger.property(clusterId());
        Properties properties = new Admin(property).properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUIDUtils.groupId());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, this.offset);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Lists.of(topic));
        while (true) {
            ConsumerRecords<byte[], byte[]> records = consumer.poll(Duration.ofMillis(100));
            if (shutdown.get()) {
                break;
            }
            for (ConsumerRecord<byte[], byte[]> record : records) {
                counts.getAndIncrement();
                callback.accept(record);
            }
        }
        consumer.close();
        return counts.get();
    }
}
