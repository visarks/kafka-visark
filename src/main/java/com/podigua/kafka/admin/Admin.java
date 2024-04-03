package com.podigua.kafka.admin;

import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import com.podigua.kafka.visark.cluster.enums.Mechanism;
import com.podigua.kafka.visark.cluster.enums.Protocal;
import com.podigua.kafka.visark.setting.SettingClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * 管理
 *
 * @author podigua
 * @date 2024/03/21
 */

public class Admin {
    /**
     * 地址
     */
    private String bootstrapServers;
    private Protocal protocol;
    private Mechanism mechanism;
    private String username;
    private String password;
    /**
     * 连接超时时间
     */
    private Integer timeout = 10000;

    public Admin(ClusterProperty property) {
        this.bootstrapServers = property.getServers();

        if(property.getSecurity()){
            this.protocol = property.getProtocal();
            this.mechanism = property.getMechanism();
            this.username = property.getUsername();
            this.password = property.getPassword();
        }

        timeout(SettingClient.get().getTimeout());
    }

    public Admin(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    /**
     * 获取地址
     *
     * @return {@link String}
     */
    public String bootstrapServers() {
        return bootstrapServers;
    }

    /**
     * 设置连接超时时间
     *
     * @param timeout 超时
     * @return {@link Admin}
     */
    public Admin timeout(int timeout) {
        this.timeout = timeout * 1000;
        return this;
    }

    /**
     * 获取超时时间
     *
     * @return int
     */
    public int timeout() {
        return timeout;
    }

    public Properties properties() {
        Properties result = new Properties();
        result.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        result.put(SaslConfigs.SASL_MECHANISM, bootstrapServers);
        return result;
    }


}
