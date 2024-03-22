package com.podigua.kafka.admin;

import com.podigua.kafka.visark.setting.SettingClient;
import javafx.concurrent.Task;
import org.apache.kafka.clients.admin.KafkaAdminClient;

/**
 * 查询任务
 *
 * @author podigua
 * @date 2024/03/22
 */
public abstract class QueryTask<T> extends Task<T> {
    private final String clusterId;

    public QueryTask(String clusterId) {
        this.clusterId = clusterId;
    }

    /**
     * 获取超时时间
     *
     * @return int
     */
    protected int timeout() {
        return SettingClient.get().getTimeout() * 1000;
    }

    /**
     * 集群 ID
     *
     * @return {@link String}
     */
    protected String clusterId() {
        return this.clusterId;
    }

    /**
     * 客户
     *
     * @return {@link KafkaAdminClient}
     */
    protected KafkaAdminClient client() {
        return AdminManger.get(clusterId);
    }
}
