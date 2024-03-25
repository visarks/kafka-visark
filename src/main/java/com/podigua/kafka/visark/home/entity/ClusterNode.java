package com.podigua.kafka.visark.home.entity;

import com.podigua.kafka.core.utils.UUIDUtils;
import com.podigua.kafka.visark.home.enums.NodeType;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.kafka.common.Node;

/**
 * 群集节点
 *
 * @author podigua
 * @date 2024/03/22
 */
public class ClusterNode {
    /**
     * 根
     *
     * @return {@link ClusterNode}
     */
    public static ClusterNode root() {
        return new ClusterNode(null, null, null, null);
    }

    /**
     * id
     */
    private final String id = UUIDUtils.uuid();

    /**
     * 集群 ID
     */
    private final String clusterId;
    /**
     * 标签
     */
    private final String label;
    /**
     * 值
     */
    private final String value;
    /**
     * 类型
     */
    private final NodeType type;
    /**
     * 加载中
     */
    private final SimpleBooleanProperty loading = new SimpleBooleanProperty(false);

    /**
     * 原生值
     */
    private Object nativeValue;

    public ClusterNode(String clusterId, String label, String value, NodeType type) {
        this.clusterId = clusterId;
        this.label = label;
        this.value = value;
        this.type = type;
    }

    /**
     * 原生值
     *
     * @param nativeValue 原生值
     * @return {@link ClusterNode}
     */
    public ClusterNode nativeValue(Object nativeValue) {
        this.nativeValue = nativeValue;
        return this;
    }

    /**
     * 原生值
     *
     * @return {@link T}
     */
    public <T> T nativeValue() {
        return (T) this.nativeValue;
    }

    /**
     * 集群 ID
     *
     * @return {@link String}
     */
    public String clusterId() {
        return this.clusterId;
    }

    /**
     * 名称
     *
     * @return {@link String}
     */
    public String label() {
        return label;
    }

    /**
     * 值
     *
     * @return {@link String}
     */
    public String value() {
        return value;
    }

    /**
     * 类型
     *
     * @return {@link NodeType}
     */
    public NodeType type() {
        return type;
    }

    /**
     * 设置加载中
     *
     * @param loading 装载
     * @return {@link ClusterNode}
     */
    public ClusterNode loading(boolean loading) {
        this.loading.set(loading);
        return this;
    }

    /**
     * 获取是否加载中
     *
     * @return boolean
     */
    public boolean loading() {
        return loading.get();
    }

    /**
     * 集群
     *
     * @param label     标签
     * @param value     价值
     * @param clusterId 集群 ID
     * @return {@link ClusterNode}
     */
    public static ClusterNode cluster(String clusterId, String label, String value) {
        return new ClusterNode(clusterId, label, value, NodeType.cluster);
    }

    /**
     * 节点文件夹
     *
     * @param clusterId 集群 ID
     * @return {@link ClusterNode}
     */
    public static ClusterNode nodes(String clusterId) {
        return new ClusterNode(clusterId, "Clusters", UUIDUtils.uuid(), NodeType.nodes);
    }

    /**
     * 节点
     *
     * @param label     标签
     * @param value     价值
     * @param clusterId 集群 ID
     * @return {@link ClusterNode}
     */
    public static ClusterNode node(String clusterId, String label, String value, Node node) {
        return new ClusterNode(clusterId, label, value, NodeType.node).nativeValue(node);
    }

    /**
     * topic文件夹
     *
     * @param clusterId 集群 ID
     * @return {@link ClusterNode}
     */
    public static ClusterNode topics(String clusterId) {
        return new ClusterNode(clusterId, "Topics", UUIDUtils.uuid(), NodeType.topics);
    }

    /**
     * topic
     *
     * @param label     标签
     * @param value     价值
     * @param clusterId 集群 ID
     * @return {@link ClusterNode}
     */
    public static ClusterNode topic(String clusterId, String label, String value) {
        return new ClusterNode(clusterId, label, value, NodeType.topic);
    }

    /**
     * consumer文件夹
     *
     * @param clusterId 集群 ID
     * @return {@link ClusterNode}
     */
    public static ClusterNode consumers(String clusterId) {
        return new ClusterNode(clusterId, "Consumers", UUIDUtils.uuid(), NodeType.consumers);
    }

    /**
     * consumer
     *
     * @param label     标签
     * @param value     价值
     * @param clusterId 集群 ID
     * @return {@link ClusterNode}
     */
    public static ClusterNode consumer(String clusterId, String label, String value) {
        return new ClusterNode(clusterId, label, value, NodeType.consumer);
    }


    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ClusterNode) {
            return id.equals(((ClusterNode) obj).id);
        }
        return false;
    }

    /**
     * 唯一键
     *
     * @return {@link String}
     */
    public String id() {
        return this.id;
    }

    @Override
    public String toString() {
        return label;
    }
}
