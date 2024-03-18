package com.podigua.kafka.visark.setting;

import com.podigua.kafka.core.debounce.Debounce;
import com.podigua.kafka.core.utils.FileUtils;
import com.podigua.kafka.core.utils.MapperUtils;
import com.podigua.kafka.visark.setting.entity.SettingProperty;
import com.podigua.kafka.visark.setting.enums.Language;
import com.podigua.kafka.visark.setting.enums.Themes;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.util.StringUtils;

import java.io.File;
import java.time.Duration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 出厂设置
 *
 * @author podigua
 * @date 2024/03/18
 */
public class SettingClient {
    private final static Debounce DEBOUNCE = new Debounce(Duration.ofMillis(200));

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
    private final static File FILE = FileUtils.file("setting.json");
    private static SettingProperty INSTANCE = new SettingProperty();

    static {
        String content = FileUtils.read(FILE);
        if (StringUtils.hasText(content)) {
            INSTANCE = MapperUtils.readValue(content, SettingProperty.class);
        }
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

    /**
     * 写
     *
     * @param setting 设置
     */
    public static void write(SettingProperty setting) {
        DEBOUNCE.execute(() -> {
            String content = MapperUtils.writeValue(setting);
            FileUtils.write(FILE, content);
            System.out.println("写入配置:" + content);
        });
    }

    public static SettingProperty get() {
        return INSTANCE;
    }
}
