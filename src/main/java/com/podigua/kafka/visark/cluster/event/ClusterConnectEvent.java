package com.podigua.kafka.visark.cluster.event;

import com.podigua.kafka.event.Event;
import com.podigua.kafka.visark.cluster.entity.ClusterProperty;

/**
 * 群集连接事件
 *
 * @author podigua
 * @date 2024/03/22
 */
public class ClusterConnectEvent extends Event {
    private final ClusterProperty property;

    public ClusterConnectEvent(ClusterProperty property) {
        this.property = property;
    }

    public ClusterProperty property() {
        return property;
    }
}
