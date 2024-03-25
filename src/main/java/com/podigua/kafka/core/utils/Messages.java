package com.podigua.kafka.core.utils;

import com.podigua.kafka.visark.setting.SettingClient;

/**
 * 消息
 *
 * @author podigua
 * @date 2024/03/25
 */
public class Messages {
    /**
     * 滤波器
     *
     * @return {@link String}
     */
    public static String filter() {
        return SettingClient.bundle().getString("message.filter");
    }

    /**
     * 偏移量
     *
     * @return {@link String}
     */
    public static String offset() {
        return SettingClient.bundle().getString("context.menu.offset");
    }

    /**
     * 成员
     *
     * @return {@link String}
     */
    public static String members() {
        return SettingClient.bundle().getString("context.menu.members");
    }
}
