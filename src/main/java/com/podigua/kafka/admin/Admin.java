package com.podigua.kafka.admin;

import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import com.podigua.kafka.visark.cluster.enums.Mechanism;
import com.podigua.kafka.visark.cluster.enums.Protocal;
import com.podigua.kafka.visark.setting.SettingClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * 管理
 *
 * @author podigua
 * @date 2024/03/21
 */

public class Admin {
    private static final Logger logger= LoggerFactory.getLogger(Admin.class);
    /**
     * 地址
     */
    private String bootstrapServers;
    /**
     * 安全
     */
    private Boolean security;
    /**
     * 协议
     */
    private Protocal protocol;
    /**
     * 机制
     */
    private Mechanism mechanism;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 连接超时时间
     */
    private Integer timeout = 10000;

    public Admin(ClusterProperty property) {
        this.bootstrapServers = property.getServers();
        this.security = property.getSecurity();
        this.protocol = property.getProtocal();
        this.mechanism = property.getMechanism();
        this.username = property.getUsername();
        this.password = property.getPassword();
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

        if(this.security){
            if(this.protocol!=null){
                result.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, this.protocol.name());
            }
            if(this.mechanism!=null){
                result.put(SaslConfigs.SASL_MECHANISM, this.mechanism.toString());
            }
            if(StringUtils.hasText(this.username) && StringUtils.hasText(this.password)){
                String jaasConfig = String.format("org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";", this.username, password);
                result.put(SaslConfigs.SASL_JAAS_CONFIG,jaasConfig);
            }
        }
        logger.info("properties:{}",result);
        return result;
    }


}
