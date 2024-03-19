package com.podigua.kafka;

import com.podigua.kafka.core.handler.DefaultExceptionHandler;
import com.podigua.kafka.core.utils.DatasourceUtils;
import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.kafka.visark.setting.entity.SettingProperty;
import com.zaxxer.hikari.HikariDataSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.flywaydb.core.Flyway;

import java.util.Locale;
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
        SettingProperty property = SettingClient.get();
        Locale.setDefault(property.getLanguage().locale());
        Application.setUserAgentStylesheet(property.getTheme().theme().getUserAgentStylesheet());
    }

    @Override
    public void start(Stage stage) throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler(stage));
        State.stage = stage;
        FXMLLoader loader = Resources.getLoader("/fxml/home.fxml");
        stage.setScene(new Scene(loader.getRoot()));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        logger.info("退出程序,释放资源");
        SettingClient.debounce().cancel();
    }
}
