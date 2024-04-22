package com.podigua.kafka.visark.cluster.entity;

import com.podigua.kafka.visark.cluster.enums.Mechanism;
import com.podigua.kafka.visark.cluster.enums.Protocal;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * 设置属性
 *
 * @author podigua
 * @date 2024/03/18
 */
public class ClusterProperty {
    private String id;
    private final SimpleStringProperty name = new SimpleStringProperty("");
    private final SimpleStringProperty servers = new SimpleStringProperty("");
    private final SimpleBooleanProperty security = new SimpleBooleanProperty(false);
    private final SimpleObjectProperty<Protocal> protocal = new SimpleObjectProperty(Protocal.SASL_PLAINTEXT);
    private final SimpleObjectProperty<Mechanism> mechanism = new SimpleObjectProperty<>(Mechanism.PLAIN);
    private final SimpleStringProperty username = new SimpleStringProperty("");
    private final SimpleStringProperty password = new SimpleStringProperty("");


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


    public void setSecurity(Boolean security) {
        this.security.set(security);
    }

    public Boolean getSecurity() {
        return security.get();
    }

    public SimpleBooleanProperty security() {
        return security;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public SimpleStringProperty username() {
        return username;
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty password() {
        return password;
    }

    public void setProtocal(Protocal protocal) {
        this.protocal.set(protocal);
    }
    public void setMechanism(Mechanism mechanism) {
        this.mechanism.set(mechanism);
    }
    public Protocal getProtocal() {
        return protocal.get();
    }

    public SimpleObjectProperty<Protocal> protocal() {
        return protocal;
    }

    public Mechanism getMechanism() {
        return mechanism.get();
    }

    public SimpleObjectProperty<Mechanism> mechanism() {
        return mechanism;
    }
}
