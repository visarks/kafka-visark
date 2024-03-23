package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.admin.ConsumerDetail;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.core.utils.Lists;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsOptions;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;

/**
 * 查询使用者任务
 *
 * @author podigua
 * @date 2024/03/22
 */
public class QuerySingleConsumerTask extends QueryTask<ConsumerGroupDescription> {
    /**
     * 组 ID
     */
    private final String groupId;


    public QuerySingleConsumerTask(String clusterId, String groupId) {
        super(clusterId);
        this.groupId = groupId;
    }

    @Override
    protected ConsumerGroupDescription call() throws Exception {
        KafkaAdminClient client = AdminManger.get(clusterId());
        DescribeConsumerGroupsResult result = client.describeConsumerGroups(Lists.of(groupId), new DescribeConsumerGroupsOptions().timeoutMs(timeout()));
        return result.all().get().get(groupId);
    }
}
