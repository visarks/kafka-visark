package com.podigua.kafka.admin;

import org.apache.kafka.common.Node;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分区
 *
 * @author podigua
 * @date 2024/03/23
 */
public class Partition {
    private final int partition;
    private final Node leader;
    private final List<Node> replicas;
    private final List<Node> isr;

    public Partition(int partition, Node leader, List<Node> replicas, List<Node> isr) {
        this.partition = partition;
        this.leader = leader;
        this.replicas = replicas;
        this.isr = isr;
    }

    /**
     * 分区
     *
     * @return int
     */
    public Integer partition() {
        return this.partition;
    }

    /**
     * 主节点
     *
     * @return Node
     */
    public String leader() {
        return this.leader.host()+":"+this.leader.port();
    }

    /**
     * 副本
     *
     * @return {@link List}<{@link Node}>
     */
    public String replicas() {
        return this.replicas.stream().filter(node -> !Node.noNode().equals(node)).map(node -> node.host() + ":" + node.port()).collect(Collectors.joining(";"));
    }

    /**
     * ISR公司
     *
     * @return {@link List}<{@link Node}>
     */
    public String isr() {
        return this.isr.stream().filter(node -> !Node.noNode().equals(node)).map(node -> node.host() + ":" + node.port()).collect(Collectors.joining(";"));
    }
}
