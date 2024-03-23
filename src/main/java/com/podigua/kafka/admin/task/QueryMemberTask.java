package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.core.utils.Lists;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询成员任务
 *
 * @author podigua
 * @date 2024/03/23
 */
public class QueryMemberTask extends QueryTask<List<MemberDescription>> {
    private final String groupId;

    public QueryMemberTask(String clusterId, String groupId) {
        super(clusterId);
        this.groupId = groupId;
    }

    @Override
    protected List<MemberDescription> call() throws Exception {
        KafkaAdminClient client = client();
        DescribeConsumerGroupsResult result = client.describeConsumerGroups(Lists.of(groupId), new DescribeConsumerGroupsOptions().timeoutMs(timeout()));
        Map<String, KafkaFuture<ConsumerGroupDescription>> groups = result.describedGroups();
        ConsumerGroupDescription description = groups.get(groupId).get();
        return new ArrayList<>(description.members());
    }
}
