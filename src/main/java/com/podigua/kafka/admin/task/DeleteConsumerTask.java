package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.core.utils.Lists;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicCollection;

/**
 * 删除Consumer任务
 *
 * @author podigua
 * @date 2024/03/23
 */
public class DeleteConsumerTask extends QueryTask<Void> {
    private final String groupId;

    public DeleteConsumerTask(String clusterId, String groupId) {
        super(clusterId);
        this.groupId = groupId;
    }

    @Override
    protected Void call() throws Exception {
        KafkaAdminClient client = AdminManger.get(clusterId());
        DeleteConsumerGroupsResult groups = client.deleteConsumerGroups(Lists.of(groupId),new DeleteConsumerGroupsOptions().timeoutMs(timeout()));
        return groups.all().get();
    }
}
