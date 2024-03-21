package com.podigua.kafka;

import com.podigua.kafka.core.handler.DefaultExceptionHandler;
import com.podigua.kafka.core.utils.DatasourceUtils;
import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.visark.setting.SettingClient;
import com.sun.javafx.tk.Toolkit;
import com.zaxxer.hikari.HikariDataSource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.flywaydb.core.Flyway;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.logging.Logger;

/**
 * 启动类
 *
 * @author podigua
 * @date 2024/03/18
 */

public class VisakApplication extends Application {
    private Logger logger = Logger.getLogger(VisakApplication.class.getSimpleName());

    @Override
    public void init() throws Exception {
        HikariDataSource datasource = DatasourceUtils.getDatasource();
        Flyway flyway = Flyway.configure().dataSource(datasource).load();
        flyway.migrate();
        SettingClient.get();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Screen screen=Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler(stage));
        State.stage = stage;
        FXMLLoader loader = Resources.getLoader("/fxml/home.fxml");
        AnchorPane root = loader.getRoot();

        Scene scene = new Scene(root,bounds.getWidth(),bounds.getHeight());
        stage.setScene(scene);
        Platform.runLater(()->{
            stage.show();
            stage.requestFocus();
        });
    }

    @Override
    public void stop() throws Exception {
        logger.info("退出程序,释放资源");
        SettingClient.debounce().cancel();
    }
}
