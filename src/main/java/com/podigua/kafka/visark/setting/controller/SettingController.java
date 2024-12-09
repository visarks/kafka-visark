package com.podigua.kafka.visark.setting.controller;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Tile;
import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import com.podigua.kafka.State;
import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.kafka.visark.setting.entity.SettingProperty;
import com.podigua.kafka.visark.setting.enums.Language;
import com.podigua.kafka.visark.setting.enums.Themes;
import com.podigua.path.Paths;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsFilled;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.util.StringUtils;

import java.io.File;
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

    private final static double WIDTH = 400;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        VBox box = new VBox();
        box.setSpacing(15);
        Tile language = language();
        Tile theme = theme();
        Tile timeout = timeout();
        Tile folder = folder();
        box.getChildren().addAll(language, theme, timeout, folder);
        this.center.getChildren().add(box);

        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);
    }

    private Tile folder() {
        Tile tile = new Tile(
                SettingClient.bundle().getString("setting.form.folder"), ""
        );
        Button select = new Button(
                "", new FontIcon(AntDesignIconsOutlined.FOLDER)
        );
        select.setCursor(Cursor.DEFAULT);
        CustomTextField folder = new CustomTextField();
        FontIcon clear = new FontIcon(AntDesignIconsFilled.CLOSE_CIRCLE);
        folder.setRight(clear);
        folder.setEditable(false);
        folder.setText(settingProperty.getDownloadFolder());

        select.getStyleClass().addAll(Styles.FONT_ICON);
        clear.setCursor(Cursor.DEFAULT);
        clear.getStyleClass().add(Styles.DANGER);
        clear.addEventHandler(MouseEvent.MOUSE_PRESSED, event->{
            folder.setText("");
            settingProperty.setDownloadFolder("");
        });
        select.setOnAction(event -> {
                    DirectoryChooser chooser = new DirectoryChooser();
                    chooser.setTitle(SettingClient.bundle().getString("chooser.select"));
                    File directory = new File(Paths.downloads());
                    if (StringUtils.hasText(folder.getText())) {
                        File current = new File(folder.getText());
                        if (current.exists()) {
                            directory = current;
                        }
                    }
                    chooser.setInitialDirectory(directory);
                    File file = chooser.showDialog(State.stage());
                    if (file != null) {
                        folder.setText(file.getAbsolutePath());
                        settingProperty.setDownloadFolder(file.getAbsolutePath());
                    }
                }

        );
        InputGroup group = new InputGroup(select,folder);
        group.setPrefWidth(WIDTH);
        folder.setPrefWidth(group.getPrefWidth() - select.getWidth());
        tile.setAction(group);
        tile.setActionHandler(group::requestFocus);
        return tile;
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
        comboBox.setPrefWidth(WIDTH);
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
        group.setPrefWidth(WIDTH);
        comboBox.setPrefWidth(group.getPrefWidth() - label.getWidth());
        group.getChildren().addAll(label, comboBox);
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
        comboBox.setPrefWidth(WIDTH);
        tile.setAction(comboBox);
        tile.setActionHandler(comboBox::requestFocus);
        return tile;
    }
}
