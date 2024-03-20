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
 * 集群表单控制器
 *
 * @author podigua
 * @date 2024/03/20
 */
public class ClusterFormController implements Initializable {
    public Button cancelButton;
    public Button saveButton;
    public Tile nameTile;
    public Tile serverTile;

    private ClusterController clusterController;

    private ClusterProperty clusterProperty;
    private Stage parent;
    private TextField nameField;
    private TextField serverField;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initStyle();
        initNameField();
        initServiceField();
    }

    private void initServiceField() {
        serverTile.setTitle(SettingClient.bundle().getString("cluster.table.servers"));
        serverField = new TextField("");
        serverField.textProperty().addListener((e, o, n) -> {
            if (this.clusterProperty != null) {
                this.clusterProperty.setServers(n);
            }
        });
        serverField.setPromptText("localhost:9092,localhost:9093");
        serverTile.setAction(serverField);
        serverTile.setActionHandler(serverField::requestFocus);
        serverField.focusedProperty().addListener((e, o, n) -> {
            serverTile.setDescription("");
        });
        serverField.setPrefWidth(300);
        serverTile.getStyleClass().add("validate");
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
        nameTile.getStyleClass().add("validate");
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

    public void onCancel(ActionEvent event) {
        this.parent.close();
    }

    public void onSave(ActionEvent event) {
        boolean success = true;
        if (!StringUtils.hasText(nameField.getText())) {
            nameTile.setDescription("[color=red]"+SettingClient.bundle().getString("cluster.name.required")+"[/color]");
            success = false;
        }
        if (!StringUtils.hasText(serverField.getText())) {
            serverTile.setDescription("[color=red]"+SettingClient.bundle().getString("cluster.servers.required")+"[/color]");
            success = false;
        }
        if (!success) {
            return;
        }
        this.saveButton.setDisable(true);
        ClusterClient.save(this.clusterProperty);
        this.clusterController.reload();
        this.parent.close();
        this.saveButton.setDisable(false);
    }

    public void set(ClusterController clusterController, Stage parent, ClusterProperty property) {
        this.clusterProperty = property;
        this.nameField.setText(property.getName());
        this.serverField.setText(property.getServers());
        this.clusterController = clusterController;
        this.parent = parent;
    }
}
