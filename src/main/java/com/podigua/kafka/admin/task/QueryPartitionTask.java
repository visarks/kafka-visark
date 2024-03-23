package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.Partition;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.core.utils.Lists;
import org.apache.kafka.clients.admin.DescribeTopicsOptions;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartitionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询分区数任务
 *
 * @author podigua
 * @date 2024/03/23
 */
public class QueryPartitionTask extends QueryTask<List<Partition>> {
    private final String topic;

    public QueryPartitionTask(String clusterId, String topic) {
        super(clusterId);
        this.topic = topic;
    }

    @Override
    protected List<Partition> call() throws Exception {
        KafkaAdminClient client = client();
        DescribeTopicsResult topics = client.describeTopics(Lists.of(topic), new DescribeTopicsOptions().timeoutMs(timeout()));
        Map<String, TopicDescription> descriptions = topics.allTopicNames().get();
        TopicDescription description = descriptions.get(topic);
        List<TopicPartitionInfo> partitions = description.partitions();
        List<Partition> result = new ArrayList<>();
        for (TopicPartitionInfo partition : partitions) {
            result.add(new Partition(partition.partition(),partition.leader(),partition.replicas(),partition.isr()));
        }
        return result;
    }
}
