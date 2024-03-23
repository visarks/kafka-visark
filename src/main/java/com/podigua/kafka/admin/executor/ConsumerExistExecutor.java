package com.podigua.kafka.admin.executor;

import com.podigua.kafka.admin.task.QuerySingleConsumerTask;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;

/**
 * 消费者存在执行程序
 *
 * @author podigua
 * @date 2024/03/24
 */
public class ConsumerExistExecutor extends TaskExecutor<ConsumerGroupDescription>{
    public ConsumerExistExecutor(String clusterId,String groupId) {
        super(new QuerySingleConsumerTask(clusterId,groupId));
    }
}
