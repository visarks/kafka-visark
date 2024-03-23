package com.podigua.kafka.admin;

import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.MemberDescription;
import org.apache.kafka.common.ConsumerGroupState;
import org.apache.kafka.common.Node;

import java.util.Collection;

/**
 * 消费者详细信息
 *
 * @author podigua
 * @date 2024/03/24
 */
public class ConsumerDetail {
    private String groupId;
    private boolean isSimpleConsumerGroup;
    private Collection<MemberDescription> members;
    private String partitionAssignor;
    private ConsumerGroupState state = ConsumerGroupState.UNKNOWN;
    private Node coordinator = Node.noNode();

    public ConsumerDetail() {

    }

    public ConsumerDetail(ConsumerGroupDescription description) {
        this.groupId = description.groupId();
        this.isSimpleConsumerGroup = description.isSimpleConsumerGroup();
        this.members = description.members();
        this.partitionAssignor = description.partitionAssignor();
        this.state = description.state();
        this.coordinator = description.coordinator();
    }

    /**
     * 重置
     *
     * @param description 描述
     */
    public void reset(ConsumerGroupDescription description) {
        this.groupId = description.groupId();
        this.isSimpleConsumerGroup = description.isSimpleConsumerGroup();
        this.members = description.members();
        this.partitionAssignor = description.partitionAssignor();
        this.state = description.state();
        this.coordinator = description.coordinator();
    }


    public String groupId() {
        return groupId;
    }

    public boolean isSimpleConsumerGroup() {
        return isSimpleConsumerGroup;
    }

    public Collection<MemberDescription> members() {
        return members;
    }

    public String partitionAssignor() {
        return partitionAssignor;
    }

    public ConsumerGroupState state() {
        return state;
    }

    public Node coordinator() {
        return coordinator;
    }
}
