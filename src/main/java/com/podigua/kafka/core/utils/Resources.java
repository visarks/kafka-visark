package com.podigua.kafka.core.utils;

import com.podigua.kafka.visark.setting.SettingClient;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 **/
public class Resources {
    private final static Map<Class<?>, Object> CACHE = new ConcurrentHashMap();

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
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> clazz) {
                Object o = CACHE.get(clazz);
                if(o==null){
                    try {
                        o=clazz.newInstance();
                        CACHE.put(clazz,o);
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                return o;
            }
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
