package com.podigua.kafka.visark.setting.entity;

import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.kafka.visark.setting.enums.Language;
import com.podigua.kafka.visark.setting.enums.Themes;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;

import java.util.Locale;

/**
 * 设置属性
 *
 * @author podigua
 * @date 2024/03/18
 */
public class SettingProperty {

    public SettingProperty() {

    }

    public void addListener() {
        this.language.addListener((observable, oldValue, newValue) -> {
            Locale.setDefault(newValue.locale());
            SettingClient.updateLocale(newValue.locale());
            SettingClient.write(this);
        });
        this.theme.addListener((observable, oldValue, newValue) -> {
            Application.setUserAgentStylesheet(newValue.theme().getUserAgentStylesheet());
            SettingClient.write(this);
        });
    }

    /**
     * 语言
     */
    private final SimpleObjectProperty<Language> language = new SimpleObjectProperty<>(Language.zh_cn);
    /**
     * 主题
     */
    private final SimpleObjectProperty<Themes> theme = new SimpleObjectProperty<>(Themes.primer_light);


    public Language getLanguage() {
        return language.get();
    }

    public void setLanguage(Language language) {
        this.language.set(language);
    }

    public SimpleObjectProperty<Language> language() {
        return this.language;
    }

    public Themes getTheme() {
        return theme.get();
    }

    public void setTheme(Themes theme) {
        this.theme.set(theme);
    }

    public SimpleObjectProperty<Themes> theme() {
        return this.theme;
    }
}
