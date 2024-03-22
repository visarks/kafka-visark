package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.concurrent.Task;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.Node;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询Topic任务
 *
 * @author podigua
 * @date 2024/03/22
 */
public class QueryTopicsTask extends QueryTask<List<ClusterNode>> {


    public QueryTopicsTask(String clusterId) {
        super(clusterId);
    }

    @Override
    protected List<ClusterNode> call() throws Exception {
        KafkaAdminClient client = AdminManger.get(clusterId());
        ListTopicsResult result = client.listTopics(new ListTopicsOptions().timeoutMs(timeout()));
        Collection<TopicListing> topics = result.listings().get();
        return topics.stream().map(data -> ClusterNode.topic(clusterId(),data.name(), data.topicId().toString())).sorted(Comparator.comparing(ClusterNode::label)).collect(Collectors.toList());
    }
}
