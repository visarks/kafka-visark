package com.podigua.kafka.visark.setting;

import com.podigua.kafka.core.debounce.Debounce;
import com.podigua.kafka.core.utils.DatasourceUtils;
import com.podigua.kafka.core.utils.FileUtils;
import com.podigua.kafka.visark.setting.entity.SettingProperty;
import com.podigua.kafka.visark.setting.enums.Language;
import com.podigua.kafka.visark.setting.enums.Themes;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.time.Duration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * 出厂设置
 *
 * @author podigua
 * @date 2024/03/18
 */
public class SettingClient {
    private final static String INSERT = "insert into setting(id,language,theme) values ('1','%s','%s')";
    private final static String UPDATE = "update setting set language='%s',theme='%s' where id='1'";
    private final static Logger logger = Logger.getLogger(SettingClient.class.getName());
    private final static Debounce DEBOUNCE = new Debounce(Duration.ofMillis(100));
    public static ObservableList<Themes> THEMES = FXCollections.observableArrayList(
            Themes.primer_light,
            Themes.primer_dark,
            Themes.nord_light,
            Themes.nord_dark,
            Themes.cupertino_light,
            Themes.cupertino_dark,
            Themes.dracula
    );


    private static ResourceBundle RESOURCE_BUNDLE;

    public static ObservableList<Language> LANGUAGES = FXCollections.observableArrayList(
            Language.zh_cn,
            Language.zh_taiwan,
            Language.english
    );
    private static SettingProperty INSTANCE=null;

    static {
        SettingProperty property = DatasourceUtils.query4Object("select * from setting where id='1'", SettingProperty.class);
        if (property == null) {
            property = new SettingProperty();
            DatasourceUtils.execute(String.format(INSERT, property.getLanguage().name(), property.getTheme().name()));
        }
        INSTANCE = property;
        Locale.setDefault(INSTANCE.getLanguage().locale());
        Application.setUserAgentStylesheet(INSTANCE.getTheme().theme().getUserAgentStylesheet());
        RESOURCE_BUNDLE = ResourceBundle.getBundle("messages", Locale.getDefault());
        INSTANCE.addListener();
    }

    public static void updateLocale(Locale locale) {
        RESOURCE_BUNDLE = ResourceBundle.getBundle("messages", Locale.getDefault());
    }

    public static ResourceBundle bundle() {
        return RESOURCE_BUNDLE;
    }

    public static Debounce debounce() {
        return DEBOUNCE;
    }

    /**
     * 写
     *
     * @param setting 设置
     */
    public static void update(SettingProperty setting) {
        DEBOUNCE.execute(() -> {
            String language = setting.getLanguage().name();
            String theme = setting.getTheme().name();
            DatasourceUtils.execute(String.format(UPDATE, language, theme));
            logger.info("更新配置");
        });
    }

    public static SettingProperty get() {
        return INSTANCE;
    }
}
