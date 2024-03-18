package com.podigua.kafka.core.utils;

import com.podigua.kafka.visark.setting.SettingClient;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

/**
 *
 **/
public class Resources {

    public static URL getResource(String name) {
        return Resources.class.getResource(name);
    }

    /**
     * 装载 机
     *
     * @param path 路径
     * @return {@link FXMLLoader}
     */
    public  static  FXMLLoader getLoader(String path) {
        FXMLLoader loader = new FXMLLoader(Resources.getResource(path));
        loader.setResources(SettingClient.bundle());
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return loader;

    }
}
