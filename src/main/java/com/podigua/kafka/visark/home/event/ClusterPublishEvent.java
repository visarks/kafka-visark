package com.podigua.kafka.visark.home.event;

import com.podigua.kafka.event.Event;
import com.podigua.kafka.visark.home.entity.ClusterNode;

/**
 * 群集发布事件
 *
 * @author podigua
 * @date 2024/03/24
 */
public class ClusterPublishEvent extends Event {
    /**
     * 节点
     */
    private final ClusterNode node;

    /**
     * 主题 DB Click 事件
     *
     * @param node 节点
     */
    public ClusterPublishEvent(ClusterNode node) {
        this.node = node;
    }

    /**
     * 节点
     *
     * @return {@link ClusterNode}
     */
    public ClusterNode node() {
        return node;
    }
}
