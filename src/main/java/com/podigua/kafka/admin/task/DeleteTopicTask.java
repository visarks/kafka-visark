package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.core.utils.Lists;
import org.apache.kafka.clients.admin.DeleteTopicsOptions;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.common.TopicCollection;

/**
 * 删除主题任务
 *
 * @author podigua
 * @date 2024/03/23
 */
public class DeleteTopicTask extends QueryTask<Void> {
    private final String topic;

    public DeleteTopicTask(String clusterId, String topic) {
        super(clusterId);
        this.topic = topic;
    }

    @Override
    protected Void call() throws Exception {
        KafkaAdminClient client = AdminManger.get(clusterId());
        TopicCollection.TopicNameCollection collection = TopicCollection.ofTopicNames(Lists.of(topic));
        DeleteTopicsResult result = client.deleteTopics(collection, new DeleteTopicsOptions().timeoutMs(timeout()));
        return result.all().get();
    }
}
