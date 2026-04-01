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
import java.util.concurrent.CompletableFuture;
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

        // 并行查询：consumer group offsets 和 group description
        CompletableFuture<Map<TopicPartition, OffsetAndMetadata>> offsetsFuture =
                client.listConsumerGroupOffsets(this.groupId)
                        .partitionsToOffsetAndMetadata()
                        .toCompletionStage()
                        .toCompletableFuture();
        CompletableFuture<Map<TopicPartition, Description>> descriptionsFuture =
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return getDescriptions();
                    } catch (Exception e) {
                        return new HashMap<>();
                    }
                });

        // 等待两个查询完成
        CompletableFuture.allOf(offsetsFuture, descriptionsFuture).join();

        Map<TopicPartition, OffsetAndMetadata> metadata = offsetsFuture.get();
        Map<TopicPartition, Description> descriptions = descriptionsFuture.get();

        Set<TopicPartition> partitions = metadata.keySet();
        List<String> topics = partitions.stream().map(TopicPartition::topic).distinct().toList();

        // 并行查询所有 topic 的 offsets
        List<CompletableFuture<List<TopicOffset>>> topicOffsetFutures = topics.stream()
                .map(topic -> CompletableFuture.supplyAsync(() -> AdminManger.getTopicOffsets(clusterId(), topic)))
                .toList();
        CompletableFuture.allOf(topicOffsetFutures.toArray(new CompletableFuture[0])).join();

        // 收集所有 topic offsets
        Map<TopicPartition, TopicOffset> topicOffsetMap = new HashMap<>();
        for (CompletableFuture<List<TopicOffset>> future : topicOffsetFutures) {
            for (TopicOffset to : future.get()) {
                topicOffsetMap.put(to.topicPartition(), to);
            }
        }

        // 构建结果
        List<ConsumerOffset> result = new ArrayList<>();
        for (TopicPartition tp : partitions) {
            TopicOffset topicOffset = topicOffsetMap.get(tp);
            if (topicOffset != null) {
                Description desc = descriptions.getOrDefault(tp, Description.create());
                result.add(new ConsumerOffset(
                        tp, desc.host, desc.memberId, desc.clientId,
                        topicOffset.start(), topicOffset.end(), metadata.get(tp).offset()
                ));
            }
        }

        return result.stream()
                .sorted(Comparator.comparing(ConsumerOffset::topic).thenComparing(ConsumerOffset::partition))
                .collect(Collectors.toList());
    }

    private Map<TopicPartition, Description> getDescriptions() {
        Map<TopicPartition, Description> result = new HashMap<>();
        try {
            DescribeConsumerGroupsResult groups = client().describeConsumerGroups(
                    Lists.of(groupId), new DescribeConsumerGroupsOptions().timeoutMs(timeout()));
            ConsumerGroupDescription description = groups.all().get().get(groupId);
            for (MemberDescription member : description.members()) {
                for (TopicPartition tp : member.assignment().topicPartitions()) {
                    result.put(tp, new Description(member.host(), member.consumerId(), member.clientId()));
                }
            }
        } catch (Exception e) {
            // 返回空 map，不影响结果
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
