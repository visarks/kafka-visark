package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.core.utils.UUIDUtils;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupsOptions;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询使用者任务
 *
 * @author podigua
 * @date 2024/03/22
 */
public class QueryConsumersTask extends QueryTask<List<ClusterNode>> {


    public QueryConsumersTask(String clusterId) {
        super(clusterId);
    }

    @Override
    protected List<ClusterNode> call() throws Exception {
        KafkaAdminClient client = AdminManger.get(clusterId());
        ListConsumerGroupsResult result = client.listConsumerGroups(new ListConsumerGroupsOptions().timeoutMs(timeout()));
        Collection<ConsumerGroupListing> consumers = result.all().get();
        return consumers.stream().map(node -> ClusterNode.consumer(clusterId(),node.groupId(), UUIDUtils.uuid())).sorted(Comparator.comparing(ClusterNode::label)).collect(Collectors.toList());
    }
}
