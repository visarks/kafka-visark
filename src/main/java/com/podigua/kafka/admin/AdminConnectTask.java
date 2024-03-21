package com.podigua.kafka.admin;

import javafx.concurrent.Task;
import org.apache.kafka.clients.admin.KafkaAdminClient;

/**
 * KafkaAdminClient连接任务
 *
 * @author podigua
 * @date 2024/03/21
 */
public class AdminConnectTask extends Task<KafkaAdminClient> {
    private final Admin admin;

    public AdminConnectTask(Admin admin) {
        this.admin = admin;
    }

    @Override
    protected KafkaAdminClient call() throws Exception {
        return AdminManger.connect(admin);
    }
}
