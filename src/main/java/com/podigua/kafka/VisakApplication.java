package com.podigua.kafka;

import com.podigua.kafka.core.handler.DefaultExceptionHandler;
import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 启动类
 *
 * @author podigua
 * @date 2024/03/18
 */
public class VisakApplication extends Application {
    @Override
    public void init() throws Exception {
        SettingClient.get();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler(stage));
        State.stage = stage;
        FXMLLoader loader = Resources.getLoader("/fxml/home.fxml");
        stage.setScene(new Scene(loader.getRoot()));
        stage.show();
    }
}
