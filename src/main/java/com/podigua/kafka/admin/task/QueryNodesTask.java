package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.common.Node;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询节点任务
 *
 * @author podigua
 * @date 2024/03/22
 */
public class QueryNodesTask extends QueryTask<List<ClusterNode>> {


    public QueryNodesTask(String clusterId) {
        super(clusterId);
    }

    @Override
    protected List<ClusterNode> call() throws Exception {
        KafkaAdminClient client = AdminManger.get(clusterId());
        DescribeClusterResult cluster = client.describeCluster(new DescribeClusterOptions().timeoutMs(timeout()));
        Collection<Node> nodes = cluster.nodes().get();
        return nodes.stream().map(node -> ClusterNode.node(clusterId(), node.host() + ":" + node.port(), node.idString(), node)).sorted(Comparator.comparing(ClusterNode::label)).collect(Collectors.toList());
    }
}
