package com.podigua.kafka;

import com.podigua.kafka.core.handler.DefaultExceptionHandler;
import com.podigua.kafka.core.utils.DatasourceUtils;
import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.event.EventBus;
import com.podigua.kafka.event.ExitPublishEvent;
import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.kafka.visark.setting.ThemeChangeEvent;
import com.podigua.kafka.visark.setting.entity.SettingProperty;
import com.podigua.kafka.visark.setting.enums.Themes;
import com.podigua.path.Paths;
import com.zaxxer.hikari.HikariDataSource;
import javafx.application.Application;
import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 启动类
 *
 * @author podigua
 * @date 2024/03/18
 */

public class VisakApplication extends Application {
    private static Logger logger = LoggerFactory.getLogger(VisakApplication.class);

    @Override
    public void init() throws Exception {
        HikariDataSource datasource = DatasourceUtils.getDatasource();
        Flyway flyway = Flyway.configure().dataSource(datasource).load();
        flyway.migrate();
        Platform.Preferences preferences = Platform.getPreferences();
        preferences.colorSchemeProperty().addListener((observable, oldValue, newValue) -> new ThemeChangeEvent().publish());
        SettingClient.get();
        subscribe();
        new ThemeChangeEvent().publish();
    }

    private void subscribe() {
        onThemeChange();
    }
    private static void onThemeChange() {
        EventBus.getInstance().subscribe(ThemeChangeEvent.class, event -> {
            SettingProperty property = SettingClient.get();
            if (property.getAutoTheme()) {
                ColorScheme scheme = Platform.getPreferences().getColorScheme();
                if (ColorScheme.DARK.equals(scheme)) {
                    Application.setUserAgentStylesheet(Themes.primer_dark.theme().getUserAgentStylesheet());
                } else {
                    Application.setUserAgentStylesheet(Themes.primer_light.theme().getUserAgentStylesheet());
                }
            } else {
                Application.setUserAgentStylesheet(property.getTheme().theme().getUserAgentStylesheet());
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler(stage));
        State.stage = stage;
        State.hostServices = getHostServices();
        FXMLLoader loader = Resources.getLoader("/fxml/home.fxml");
        AnchorPane root = loader.getRoot();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinHeight(766);
        stage.setMinWidth(1216);
        stage.setMaximized(true);
        stage.getIcons().add(new Image(Resources.getResource("/images/logo.png").toExternalForm()));
        Platform.runLater(() -> {
            stage.show();
            stage.requestFocus();
        });
    }

    @Override
    public void stop() throws Exception {
        logger.info("退出程序,释放资源");
        EventBus.getInstance().publish(new ExitPublishEvent());
        SettingClient.debounce().cancel();
    }
}
