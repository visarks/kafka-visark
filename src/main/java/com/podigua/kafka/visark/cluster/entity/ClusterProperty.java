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
    private  String id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
