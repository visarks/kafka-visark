package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.core.utils.UUIDUtils;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import org.apache.kafka.clients.admin.GroupListing;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListGroupsOptions;
import org.apache.kafka.clients.admin.ListGroupsResult;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询使用者任务
 *
 * @author podigua
 */
public class  QueryConsumersTask extends QueryTask<List<ClusterNode>> {

    public QueryConsumersTask(String clusterId) {
        super(clusterId);
    }

    @Override
    protected List<ClusterNode> call() throws Exception {
        KafkaAdminClient client = AdminManger.get(clusterId());
        // 使用新的 listGroups API，过滤只获取 consumer groups
        ListGroupsResult result = client.listGroups(
                ListGroupsOptions.forConsumerGroups().timeoutMs(timeout())
        );
        Collection<GroupListing> groups = result.all().get();
        return groups.stream()
                .map(group -> ClusterNode.consumer(clusterId(), group.groupId(), UUIDUtils.uuid()))
                .sorted(Comparator.comparing(ClusterNode::label))
                .collect(Collectors.toList());
    }
}
