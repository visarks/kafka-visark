package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.admin.TopicOffset;

import java.util.List;

/**
 * 查询主题偏移任务
 *
 * @author podigua
 * @date 2024/03/23
 */
public class QueryTopicOffsetTask extends QueryTask<List<TopicOffset>> {
    /**
     * 主题
     */
    private final String topic;

    public QueryTopicOffsetTask(String clusterId, String topic) {
        super(clusterId);
        this.topic = topic;
    }

    @Override
    protected List<TopicOffset> call() throws Exception {
        return AdminManger.getTopicOffsets(this.clusterId(), this.topic);
    }
}
