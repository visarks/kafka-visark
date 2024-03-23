package com.podigua.kafka.admin.executor;

import com.podigua.kafka.admin.Partition;
import com.podigua.kafka.admin.task.QueryPartitionTask;

import java.util.List;

/**
 * 获取分区任务执行器
 *
 * @author podigua
 * @date 2024/03/24
 */
public class GetPartitionTaskExecutor extends TaskExecutor<List<Partition>> {

    public GetPartitionTaskExecutor(String cluster, String topic) {
        super(new QueryPartitionTask(cluster, topic));
    }
}
