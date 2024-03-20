package com.podigua.kafka;

import com.podigua.kafka.core.handler.DefaultExceptionHandler;
import com.podigua.kafka.core.utils.DatasourceUtils;
import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.visark.setting.SettingClient;
import com.zaxxer.hikari.HikariDataSource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler(stage));
        State.stage = stage;
        FXMLLoader loader = Resources.getLoader("/fxml/home.fxml");
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setOnShown(event -> {
            stage.toFront(); // 将舞台移到所有窗口的前面
            stage.requestFocus(); // 请求焦点
            Platform.runLater(() -> {
                // 进一步尝试模拟键盘或鼠标事件以激活窗口
                // 注意，这可能因操作系统版本而异
//                try {
//                    Robot robot = new Robot();
//                    robot.mouseMove((int)stage.getX() + 100, (int)stage.getY() + 100); // 移动鼠标到窗口内
//                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
//                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//                } catch (Exception e) {
//
//                }
            });
        });
        Platform.runLater(stage::show);


    }

    @Override
    public void stop() throws Exception {
        logger.info("退出程序,释放资源");
        SettingClient.debounce().cancel();
    }
}
