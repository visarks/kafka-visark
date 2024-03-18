package com.podigua.kafka.visark.cluster.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * 设置属性
 *
 * @author podigua
 * @date 2024/03/18
 */
public class ClusterProperty {
    private final SimpleStringProperty priority = new SimpleStringProperty("1");
    private final SimpleStringProperty name = new SimpleStringProperty("");
    private final SimpleStringProperty servers = new SimpleStringProperty("");

    public void setName(String name) {
        this.name.set(name);
    }

    public void setServers(String servers) {
        this.servers.set(servers);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty name() {
        return name;
    }

    public String getServers() {
        return servers.get();
    }

    public SimpleStringProperty servers() {
        return servers;
    }

    public void setPriority(String priority) {
        this.priority.set(priority);
    }

    public String getPriority() {
        return priority.get();
    }

    public SimpleStringProperty priority() {
        return priority;
    }
}
