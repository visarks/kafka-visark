package com.podigua.kafka.visark.setting.entity;

import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.kafka.visark.setting.ThemeChangeEvent;
import com.podigua.kafka.visark.setting.enums.Language;
import com.podigua.kafka.visark.setting.enums.Themes;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

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
            SettingClient.update(this);
            new ThemeChangeEvent().publish();
        });
        this.timeout.addListener((observable, oldValue, newValue) -> {
            SettingClient.update(this);
        });
        this.autoTheme.addListener((observable, oldValue, newValue) -> {
            SettingClient.update(this);
            new ThemeChangeEvent().publish();
        });
        this.openDialog.addListener((observable, oldValue, newValue) -> {
            SettingClient.update(this);
        });
        this.downloadFolder.addListener((observable, oldValue, newValue) -> {
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
    private final SimpleIntegerProperty timeout = new SimpleIntegerProperty(60);

    /**
     * 自动主题
     */
    private final SimpleBooleanProperty autoTheme = new SimpleBooleanProperty(false);
    /**
     * 打开对话框
     */
    private final SimpleBooleanProperty openDialog = new SimpleBooleanProperty(false);
    /**
     * 下载文件夹
     */
    private final SimpleStringProperty downloadFolder = new SimpleStringProperty("");


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


    public void setAutoTheme(Boolean autoTheme) {
        this.autoTheme.set(autoTheme);
    }

    public Boolean getAutoTheme() {
        return autoTheme.get();
    }

    public SimpleBooleanProperty autoTheme() {
        return autoTheme;
    }

    public void setOpenDialog(Boolean openDialog) {
        this.openDialog.set(openDialog);
    }

    public Boolean getOpenDialog() {
        return openDialog.get();
    }

    public SimpleBooleanProperty openDialog() {
        return openDialog;
    }

    public void setDownloadFolder(String downloadFolder) {
        this.downloadFolder.set(downloadFolder);
    }


    public String getDownloadFolder() {
        return downloadFolder.get();
    }

    public SimpleStringProperty downloadFolderProperty() {
        return downloadFolder;
    }
}
