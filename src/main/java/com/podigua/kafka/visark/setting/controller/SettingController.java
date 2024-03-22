package com.podigua.kafka.visark.setting.controller;

import atlantafx.base.controls.Tile;
import atlantafx.base.layout.InputGroup;
import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.kafka.visark.setting.entity.SettingProperty;
import com.podigua.kafka.visark.setting.enums.Language;
import com.podigua.kafka.visark.setting.enums.Themes;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 设置控制器
 *
 * @author podigua
 * @date 2024/03/18
 */
public class SettingController implements Initializable {
    private final SettingProperty settingProperty = SettingClient.get();
    public AnchorPane center;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        VBox box = new VBox();
        box.setSpacing(15);
        Tile language = language();
        Tile theme = theme();
        Tile timeout = timeout();
        box.getChildren().addAll(language, theme, timeout);
        this.center.getChildren().add(box);

        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);
    }

    private Tile timeout() {
        Tile tile = new Tile(
                SettingClient.bundle().getString("setting.form.timeout"), ""
        );
        ComboBox<Integer> comboBox = new ComboBox<>(new SimpleListProperty<>(FXCollections.observableArrayList(10, 30, 60, 120)));
        comboBox.setValue(settingProperty.getTimeout());
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            settingProperty.timeout().set(newValue);
        });
        comboBox.setPrefWidth(220);
        tile.setAction(comboBox);
        tile.setActionHandler(comboBox::requestFocus);
        return tile;
    }

    private Tile theme() {
        Tile tile = new Tile(
                SettingClient.bundle().getString("setting.form.theme"), ""
        );
        InputGroup group = new InputGroup();
        CheckBox check = new CheckBox();
        Label label = new Label("", check);
        label.setTooltip(new Tooltip(SettingClient.bundle().getString("setting.form.theme.auto")));
        check.setSelected(settingProperty.getAutoTheme());
        check.selectedProperty().addListener((observable, oldValue, newValue) -> {
            settingProperty.setAutoTheme(newValue);
        });
        ComboBox<Themes> comboBox = new ComboBox<>(new SimpleListProperty<>(SettingClient.THEMES));
        comboBox.setValue(settingProperty.getTheme());
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            settingProperty.theme().set(newValue);
        });
        comboBox.setDisable(check.isSelected());
        check.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                comboBox.setDisable(true);
            } else {
                comboBox.setDisable(false);
            }
        });
        group.setPrefWidth(220);
        comboBox.setPrefWidth(group.getPrefWidth()-label.getWidth());
        group.getChildren().addAll(label,comboBox);
        tile.setAction(group);
        tile.setActionHandler(group::requestFocus);
        return tile;
    }

    private Tile language() {
        Tile tile = new Tile(
                SettingClient.bundle().getString("setting.form.language"),
                SettingClient.bundle().getString("setting.form.language.prompt")
        );
        ComboBox<Language> comboBox = new ComboBox<>(new SimpleListProperty<>(SettingClient.LANGUAGES));
        comboBox.setValue(settingProperty.getLanguage());
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            settingProperty.language().set(newValue);
        });
        comboBox.setPrefWidth(220);
        tile.setAction(comboBox);
        tile.setActionHandler(comboBox::requestFocus);
        return tile;
    }
}
