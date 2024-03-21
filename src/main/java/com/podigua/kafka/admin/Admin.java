package com.podigua.kafka.admin;

import org.apache.kafka.clients.admin.AdminClientConfig;

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
    /**
     * 连接超时时间
     */
    private Integer timeout = 10000;

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
        return result;
    }


}
