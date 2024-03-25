package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.admin.ConsumerOffset;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.admin.TopicOffset;
import com.podigua.kafka.core.utils.Lists;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 查询Consumer偏移任务
 *
 * @author podigua
 * @date 2024/03/24
 */
public class QueryConsumerOffsetTask extends QueryTask<List<ConsumerOffset>> {
    /**
     * 组 ID
     */
    private final String groupId;

    public QueryConsumerOffsetTask(String clusterId, String groupId) {
        super(clusterId);
        this.groupId = groupId;
    }

    @Override
    protected List<ConsumerOffset> call() throws Exception {
        KafkaAdminClient client = client();
        ListConsumerGroupOffsetsResult offsets = client.listConsumerGroupOffsets(this.groupId);
        Map<TopicPartition, OffsetAndMetadata> metadata = offsets.partitionsToOffsetAndMetadata().get();
        Set<TopicPartition> partitions = metadata.keySet();
        List<String> topics = partitions.stream().map(TopicPartition::topic).distinct().toList();
        List<ConsumerOffset> result = new ArrayList<>();
        Map<TopicPartition, Description> descriptions = getDescriptions();
        for (String topic : topics) {
            List<TopicOffset> list = AdminManger.getTopicOffsets(clusterId(), topic);
            for (TopicOffset topicOffset : list) {
                if (metadata.containsKey(topicOffset.topicPartition())) {
                    Description description = descriptions.getOrDefault(topicOffset.topicPartition(), Description.create());
                    result.add(new ConsumerOffset(topicOffset.topicPartition(), description.host, description.memberId, description.clientId, topicOffset.start(), topicOffset.end(), metadata.get(topicOffset.topicPartition()).offset()));
                }
            }
        }
        return result.stream().sorted(Comparator.comparing(ConsumerOffset::topic).thenComparing(ConsumerOffset::partition)).collect(Collectors.toList());
    }

    private Map<TopicPartition, Description> getDescriptions() throws InterruptedException, ExecutionException {
        Map<TopicPartition, Description> result = new HashMap<>();
        DescribeConsumerGroupsResult groups = client().describeConsumerGroups(Lists.of(groupId), new DescribeConsumerGroupsOptions().timeoutMs(timeout()));
        ConsumerGroupDescription description = groups.all().get().get(groupId);
        Collection<MemberDescription> members = description.members();
        for (MemberDescription member : members) {
            MemberAssignment assignment = member.assignment();
            for (TopicPartition topicPartition : assignment.topicPartitions()) {
                result.put(topicPartition, new Description(member.host(), member.consumerId(), member.clientId()));
            }
        }
        return result;
    }

    /**
     * 描述
     *
     * @author podigua
     * @date 2024/03/25
     */
    private record Description(String host, String memberId, String clientId) {

        public static Description create() {
            return new Description("", "", "");
        }
    }
}
