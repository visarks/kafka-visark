package com.podigua.kafka.visark.setting.entity;

import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.kafka.visark.setting.enums.Language;
import com.podigua.kafka.visark.setting.enums.Themes;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

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
            SettingClient.updateLocale();
            SettingClient.update(this);
        });
        this.theme.addListener((observable, oldValue, newValue) -> {
            Application.setUserAgentStylesheet(newValue.theme().getUserAgentStylesheet());
            SettingClient.update(this);
        });
        this.timeout.addListener((observable, oldValue, newValue) -> {
            SettingClient.update(this);
        });
    }

    private String id = "1";

    /**
     * 语言
     */
    private final SimpleObjectProperty<Language> language = new SimpleObjectProperty<>(Language.zh_cn);
    /**
     * 主题
     */
    private final SimpleObjectProperty<Themes> theme = new SimpleObjectProperty<>(Themes.primer_light);

    /**
     * 超时时间
     */
    private final SimpleIntegerProperty timeout = new SimpleIntegerProperty(10);


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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTimeout(Integer timeout) {
        this.timeout.set(timeout);
    }

    public Integer getTimeout() {
        return timeout.get();
    }

    public SimpleIntegerProperty timeout() {
        return timeout;
    }
}
