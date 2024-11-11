package com.podigua.kafka;

import com.podigua.kafka.about.AbortPane;
import com.podigua.kafka.core.handler.DefaultExceptionHandler;
import com.podigua.kafka.core.utils.DatasourceUtils;
import com.podigua.kafka.core.utils.FileUtils;
import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.core.utils.StageUtils;
import com.podigua.kafka.event.EventBus;
import com.podigua.kafka.event.ExitPublishEvent;
import com.podigua.kafka.license.LicenseUtils;
import com.podigua.kafka.visark.home.controller.HomeController;
import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.kafka.visark.setting.ThemeChangeEvent;
import com.podigua.kafka.visark.setting.entity.SettingProperty;
import com.podigua.kafka.visark.setting.enums.Themes;
import com.sun.javafx.tk.Toolkit;
import com.zaxxer.hikari.HikariDataSource;
import de.jangassen.MenuToolkit;
import javafx.application.Application;
import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;


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
        logger.info("启动程序, args:{}", Arrays.toString(State.args()));
        logger.info("启动程序, properties:{}", System.getProperties());
        logger.info("启动程序, env:{}", System.getenv());
        HikariDataSource datasource = DatasourceUtils.getDatasource();
        Flyway flyway = Flyway.configure().dataSource(datasource).load();
        flyway.migrate();
        license();
        Platform.Preferences preferences = Platform.getPreferences();
        preferences.colorSchemeProperty().addListener((observable, oldValue, newValue) -> new ThemeChangeEvent().publish());
        SettingClient.get();
        subscribe();
        new ThemeChangeEvent().publish();
    }

    private void license() {
        File license = FileUtils.file("license");
        if (!license.exists()) {
            InputStream stream = Resources.getResourceAsStream("/license");
            try {
                FileUtils.copy(stream, new FileOutputStream(license));
            } catch (Exception e) {
                logger.error("写入license文件失败", e);
            }
        }
        State.license = LicenseUtils.decrypt(FileUtils.read(license));
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
            if (Platform.isFxApplicationThread()) {
                HomeController.play();
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        initMenu();
        boolean supported = Toolkit.getToolkit().getSystemMenu().isSupported();
        logger.info("是否支持系统菜单:{}", supported);
        Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler(stage));
        State.stage = stage;
        State.hostServices = getHostServices();
        FXMLLoader loader = Resources.getLoader("/fxml/home.fxml");
        AnchorPane root = loader.getRoot();
        State.pane = root;
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinHeight(766);
        stage.setMinWidth(1216);
        stage.setMaximized(true);
        stage.getIcons().add(new Image(Resources.getResource("/images/logo.png").toExternalForm()));
        stage.setOnShown(event -> {
            if(State.license().expire()){
                var alert = new Alert(Alert.AlertType.NONE,"许可证已过期,请联系(podigua@126.com)更新换的许可证",ButtonType.OK);
                alert.initOwner(State.stage());
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.showAndWait();
                Platform.exit();
            }
        });
        Platform.runLater(() -> {
            stage.show();
            stage.requestFocus();
        });

//        if (State.license.expire()) {
//            new Thread(()->{
//                try {
//                    Thread.sleep(3000);
//                    Platform.runLater(()->{
//
//                    });
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }).start();
//        }

    }


    private static void initMenu() {
        AbortPane pane = new AbortPane();
        Stage stage = StageUtils.none(pane);
        MenuItem about = MenuToolkit.toolkit(SettingClient.bundle().getLocale()).createAboutMenuItem(State.PRODUCT, stage);
        MenuItem hide = MenuToolkit.toolkit(SettingClient.bundle().getLocale()).createHideMenuItem(State.PRODUCT);
        MenuItem hideOthers = MenuToolkit.toolkit(SettingClient.bundle().getLocale()).createHideOthersMenuItem();
        MenuItem quit = MenuToolkit.toolkit(SettingClient.bundle().getLocale()).createQuitMenuItem(State.PRODUCT);
        Menu menu = new Menu();
        menu.getItems().addAll(about, new SeparatorMenuItem(), hide, hideOthers, new SeparatorMenuItem(), quit);
        MenuToolkit.toolkit(SettingClient.bundle().getLocale()).setApplicationMenu(menu);
    }

    @Override
    public void stop() throws Exception {
        logger.info("退出程序,释放资源");
        EventBus.getInstance().publish(new ExitPublishEvent());
        SettingClient.debounce().cancel();
    }
}
