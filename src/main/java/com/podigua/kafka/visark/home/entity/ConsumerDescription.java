package com.podigua.kafka.visark.home.entity;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.common.acl.AclOperation;

import java.util.Set;

public class ConsumerDescription {
    private SimpleStringProperty groupId = new SimpleStringProperty();
    private SimpleBooleanProperty isSimpleConsumerGroup = new SimpleBooleanProperty(false);
    private SimpleStringProperty partitionAssignor = new SimpleStringProperty();
    private SimpleStringProperty state = new SimpleStringProperty();
    private SimpleStringProperty coordinator = new SimpleStringProperty();
    private ObservableList<String> authorizedOperations = FXCollections.observableArrayList();

    public void reset(ConsumerGroupDescription description) {
        this.setGroupId(description.groupId());
        this.setIsSimpleConsumerGroup(description.isSimpleConsumerGroup());
        this.setCoordinator(description.partitionAssignor());
        this.setState(description.state().toString());
        this.setPartitionAssignor(description.partitionAssignor());
        this.authorizedOperations.clear();
        Set<AclOperation> operations = description.authorizedOperations();
        if(operations!=null){
            for (AclOperation operation : operations) {
                this.authorizedOperations.add(operation.name());
            }
        }
    }

    public String getGroupId() {
        return groupId.get();
    }

    public SimpleStringProperty groupIdProperty() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId.set(groupId);
    }

    public boolean isIsSimpleConsumerGroup() {
        return isSimpleConsumerGroup.get();
    }

    public SimpleBooleanProperty isSimpleConsumerGroupProperty() {
        return isSimpleConsumerGroup;
    }

    public void setIsSimpleConsumerGroup(boolean isSimpleConsumerGroup) {
        this.isSimpleConsumerGroup.set(isSimpleConsumerGroup);
    }

    public String getPartitionAssignor() {
        return partitionAssignor.get();
    }

    public SimpleStringProperty partitionAssignorProperty() {
        return partitionAssignor;
    }

    public void setPartitionAssignor(String partitionAssignor) {
        this.partitionAssignor.set(partitionAssignor);
    }

    public String getState() {
        return state.get();
    }

    public SimpleStringProperty stateProperty() {
        return state;
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public String getCoordinator() {
        return coordinator.get();
    }

    public SimpleStringProperty coordinatorProperty() {
        return coordinator;
    }

    public void setCoordinator(String coordinator) {
        this.coordinator.set(coordinator);
    }

    public ObservableList<String> getAuthorizedOperations() {
        return authorizedOperations;
    }

    public void setAuthorizedOperations(ObservableList<String> authorizedOperations) {
        this.authorizedOperations = authorizedOperations;
    }
}
