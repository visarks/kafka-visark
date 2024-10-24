package com.podigua.kafka.core.utils;

import com.podigua.kafka.visark.setting.SettingClient;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 资源
 *
 * @author podigua
 * @date 2024/10/24
 */
public class Resources {
    private final static Map<Class<?>, Object> CACHE = new ConcurrentHashMap<>();

    public static URL getResource(String name) {
        return Resources.class.getResource(name);
    }

    /**
     * 装载 机
     *
     * @param path 路径
     * @return {@link FXMLLoader}
     */
    public static FXMLLoader getLoader(String path) {
        FXMLLoader loader = new FXMLLoader(Resources.getResource(path));
        loader.setControllerFactory(clazz -> {
            Object o = CACHE.get(clazz);
            if (o == null) {
                try {
                    o = clazz.getDeclaredConstructor().newInstance();
                    CACHE.put(clazz, o);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return o;
        });
        loader.setResources(SettingClient.bundle());
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return loader;

    }
}
