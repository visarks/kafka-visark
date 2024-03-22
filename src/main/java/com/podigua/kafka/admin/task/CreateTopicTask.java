package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.core.utils.Lists;
import com.podigua.kafka.core.utils.UUIDUtils;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;

/**
 * 创建主题任务
 *
 * @author podigua
 * @date 2024/03/23
 */
public class CreateTopicTask extends QueryTask<ClusterNode> {
    /**
     * 主题
     */
    private final String topic;
    /**
     * 分区
     */
    private final int partition;
    /**
     * 副本数
     */
    private final short replica;

    public CreateTopicTask(String clusterId, String topic, int partition, short replica) {
        super(clusterId);
        this.topic = topic;
        this.partition = partition;
        this.replica = replica;
    }

    @Override
    protected ClusterNode call() throws Exception {
        KafkaAdminClient client = client();
        NewTopic newTopic = new NewTopic(topic, partition, replica);
        CreateTopicsResult result = client.createTopics(Lists.of(newTopic), new CreateTopicsOptions().timeoutMs(timeout()));
        result.all().get();
        return ClusterNode.topic(clusterId(), topic, UUIDUtils.uuid());
    }
}
