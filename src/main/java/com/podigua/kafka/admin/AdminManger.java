package com.podigua.kafka.admin;

import com.podigua.kafka.visark.setting.SettingClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.errors.TimeoutException;

import java.util.*;

/**
 * Admin 管理
 *
 * @author podigua
 * @date 2024/03/21
 */
public class AdminManger {
    private final static Map<String, KafkaAdminClient> CLIENTS = new HashMap<>();


    /**
     * 连接
     *
     * @param admin 管理
     * @return {@link KafkaAdminClient}
     */
    public static KafkaAdminClient connect(Admin admin) {
        KafkaAdminClient client = null;
        try {
            client = (KafkaAdminClient) AdminClient.create(admin.properties());
            DescribeClusterResult result = client.describeCluster(new DescribeClusterOptions().timeoutMs(admin.timeout()));
            KafkaFuture<Node> controller = result.controller();
            controller.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return client;
    }

    /**
     * put
     *
     * @param id     编号
     * @param client 客户
     */
    public static void put(String id, KafkaAdminClient client) {
        CLIENTS.put(id, client);
    }

    /**
     * 获取
     *
     * @param id 编号
     */
    public static KafkaAdminClient get(String id) {
        return CLIENTS.get(id);
    }

    /**
     * 删除
     *
     * @param id 编号
     */
    public static void remove(String id) {
        Optional.ofNullable(CLIENTS.get(id)).ifPresent(client -> {
            client.close();
            CLIENTS.remove(id);
        });

    }

    /**
     * 翻译 错误信息
     *
     * @param throwable 可投掷
     * @return {@link Throwable}
     */
    public static Throwable translate(Throwable throwable) {
        List<Throwable> list = new ArrayList<>();
        Throwable cause = throwable.getCause();
        while (cause != null) {
            list.add(cause);
            cause = cause.getCause();
        }
        for (Throwable t : list) {
            if (t instanceof TimeoutException) {
                return new RuntimeException(SettingClient.bundle().getString("cluster.connect.timeout"));
            }
        }
        return cause==null?throwable:cause;
    }
}
