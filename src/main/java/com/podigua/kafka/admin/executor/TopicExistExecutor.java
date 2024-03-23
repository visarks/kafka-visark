package com.podigua.kafka.admin.executor;

import com.podigua.kafka.admin.task.QueryTopicExistsTask;

/**
 * 主题存在执行器
 *
 * @author podigua
 * @date 2024/03/24
 */
public class TopicExistExecutor extends TaskExecutor<Boolean> {
    public TopicExistExecutor(String clusterId, String topic) {
        super(new QueryTopicExistsTask(clusterId, topic));
    }
}
