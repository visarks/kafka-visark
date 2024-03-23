package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.core.utils.Lists;
import org.apache.kafka.clients.admin.DescribeTopicsOptions;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.common.TopicCollection;

/**
 * 查询主题存在任务
 *
 * @author podigua
 * @date 2024/03/24
 */
public class QueryTopicExistsTask extends QueryTask<Boolean> {
    private final String topic;

    public QueryTopicExistsTask(String clusterId, String topic) {
        super(clusterId);
        this.topic = topic;
    }

    @Override
    protected Boolean call() throws Exception {
        KafkaAdminClient client = client();
        DescribeTopicsResult result = client.describeTopics(TopicCollection.ofTopicNames(Lists.of(topic)), new DescribeTopicsOptions().timeoutMs(timeout()));
        result.allTopicNames().get();
        return true;
    }
}
