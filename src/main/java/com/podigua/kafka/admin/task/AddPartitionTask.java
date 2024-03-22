package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.QueryTask;
import org.apache.kafka.clients.admin.CreatePartitionsOptions;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewPartitions;

import java.util.HashMap;
import java.util.Map;

/**
 * 添加分区任务
 *
 * @author podigua
 * @date 2024/03/23
 */
public class AddPartitionTask extends QueryTask<Void> {
    /**
     * 主题
     */
    private final String topic;
    /**
     * 分区
     */
    private final int partition;

    public AddPartitionTask(String clusterId, String topic, int partition) {
        super(clusterId);
        this.topic = topic;
        this.partition = partition;
    }

    @Override
    protected Void call() throws Exception {
        KafkaAdminClient client = client();
        Map<String, NewPartitions> partitions = new HashMap<>();
        partitions.put(topic, NewPartitions.increaseTo(partition));
        CreatePartitionsResult result = client.createPartitions(partitions, new CreatePartitionsOptions().timeoutMs(timeout()));
        return result.all().get();
    }
}
