package com.podigua.kafka.visark.cluster.event;

import com.podigua.kafka.event.Event;

/**
 * 群集断开事件
 *
 * @author podigua
 * @date 2024/03/22
 */
public class ClusterCloseEvent extends Event {
    private final String clusterId;

    public ClusterCloseEvent(String clusterId) {
        this.clusterId = clusterId;
    }
    public String clusterId() {
        return this.clusterId;
    }
}
