package com.podigua.kafka.admin;

import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import javafx.concurrent.Task;
import org.apache.kafka.clients.admin.KafkaAdminClient;

/**
 * KafkaAdminClient连接任务
 *
 * @author podigua
 * @date 2024/03/21
 */
public class AdminConnectTask extends Task<KafkaAdminClient> {
    private final ClusterProperty property;

    public AdminConnectTask(ClusterProperty property) {
        this.property = property;
    }

    @Override
    protected KafkaAdminClient call() throws Exception {
        return AdminManger.connect(property);
    }
}
