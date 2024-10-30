package com.podigua.kafka.visark.cluster.controller;

import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import com.podigua.kafka.visark.cluster.ClusterClient;
import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 **/
public class FolderFormController implements Initializable {
    public Tile nameTile;
    public Button cancelButton;
    public Button saveButton;
    private TextField nameField;
    private ClusterProperty clusterProperty;
    private ClusterController clusterController;
    private Stage parent;
    private boolean isAdd;

    public void onCancel(ActionEvent actionEvent) {
        this.parent.close();
    }

    public void onSave(ActionEvent actionEvent) {
        boolean success = true;
        if (!StringUtils.hasText(nameField.getText())) {
            nameTile.setDescription("[color=red]"+SettingClient.bundle().getString("cluster.name.required")+"[/color]");
            success = false;
        }
        if (!success) {
            return;
        }
        this.saveButton.setDisable(true);
        ClusterClient.save(this.clusterProperty);
        this.clusterController.success(this.clusterProperty,isAdd);
        this.parent.close();
        this.saveButton.setDisable(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initStyle();
        initNameField();
        this.nameField.requestFocus();
    }
    private void initStyle() {
        cancelButton.setGraphic(new FontIcon(Material2AL.CANCEL));
        cancelButton.getStyleClass().addAll(
                Styles.BUTTON_OUTLINED, Styles.TEXT
        );
        saveButton.setGraphic(new FontIcon(Material2MZ.SAVE));
        saveButton.getStyleClass().addAll(
                Styles.BUTTON_OUTLINED, Styles.ACCENT
        );
    }
    private void initNameField() {
        nameTile.setTitle(SettingClient.bundle().getString("cluster.table.name"));
        nameField = new TextField("");
        nameField.textProperty().addListener((e, o, n) -> {
            if (this.clusterProperty != null) {
                this.clusterProperty.setName(n);
            }
        });
        nameTile.setAction(nameField);
        nameTile.setActionHandler(nameField::requestFocus);
        nameField.focusedProperty().addListener((e, o, n) -> {
            nameTile.setDescription("");
        });
        nameField.setPrefWidth(300);
    }
    public void set(ClusterController clusterController, Stage parent, ClusterProperty property, boolean isAdd) {
        this.clusterProperty = property.copy();
        this.nameField.setText(property.getName());
        this.clusterController = clusterController;
        this.parent=parent;
        this.isAdd=isAdd;
    }
}
