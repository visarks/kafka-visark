package com.podigua.kafka.visark.cluster.controller;

import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import com.podigua.kafka.visark.cluster.ClusterClient;
import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import com.podigua.kafka.visark.cluster.enums.Mechanism;
import com.podigua.kafka.visark.cluster.enums.Protocal;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.beans.property.SimpleListProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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
    public Tile securityTile;
    public Tile protocalTile;
    public Tile mechanismTile;
    public Tile usernameTile;
    public Tile passwordTile;

    private ClusterController clusterController;

    private ClusterProperty clusterProperty;
    private Stage parent;
    private TextField nameField;
    private TextField serverField;
    private CheckBox securityField;
    private ComboBox<Mechanism> mechanismField;
    private TextField usernameField;
    private TextField passwordField;
    private boolean isAdd;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initStyle();
        initNameField();
        initServerField();
        initSecurityField();
        initProtocalField();
        initMechanismField();
        initUsernameField();
        initPasswordField();
        this.nameField.requestFocus();
    }

    private void initPasswordField() {
        passwordTile.setTitle(SettingClient.bundle().getString("cluster.table.password"));
        passwordField = new PasswordField();
        passwordField.textProperty().addListener((e, o, n) -> {
            if (this.clusterProperty != null) {
                this.clusterProperty.setPassword(n);
            }
        });
        passwordTile.setAction(passwordField);
        passwordTile.setActionHandler(passwordField::requestFocus);
        passwordField.focusedProperty().addListener((e, o, n) -> {
            passwordTile.setDescription("");
        });
        passwordField.setPrefWidth(300);
    }

    private void initUsernameField() {
        usernameTile.setTitle(SettingClient.bundle().getString("cluster.table.username"));
        usernameField = new TextField("");
        usernameField.textProperty().addListener((e, o, n) -> {
            if (this.clusterProperty != null) {
                this.clusterProperty.setUsername(n);
            }
        });
        usernameTile.setAction(usernameField);
        usernameTile.setActionHandler(usernameField::requestFocus);
        usernameField.focusedProperty().addListener((e, o, n) -> {
            usernameTile.setDescription("");
        });
        usernameField.setPrefWidth(300);
    }

    private void initMechanismField() {
        mechanismTile.setTitle("Mechanism");
        mechanismField = new ComboBox<>();
        mechanismField.setItems(new SimpleListProperty<>(Mechanism.MECHANISM));
        mechanismField.setValue(Mechanism.PLAIN);
        mechanismField.valueProperty().addListener((e, o, n) -> {
            if (this.clusterProperty != null) {
                this.clusterProperty.setMechanism(n);
            }
        });
        mechanismTile.setAction(mechanismField);
        mechanismTile.setActionHandler(mechanismField::requestFocus);
        mechanismField.focusedProperty().addListener((e, o, n) -> {
            mechanismTile.setDescription("");
        });
        mechanismField.setPrefWidth(300);
        mechanismTile.getStyleClass().add("validate");
    }

    private void initProtocalField() {
        protocalTile.setTitle("Protocal");
        protocalTile.setAction(new Label("SASL_PLAINTEXT"));
        protocalTile.getStyleClass().add("validate");
    }

    private void initSecurityField() {
        securityTile.setTitle("Security");
        securityField = new CheckBox();
        securityField.setSelected(false);
        securityField.selectedProperty().addListener((e, o, n) -> {
            if (this.clusterProperty != null) {
                this.clusterProperty.setSecurity(n);
            }
            if(n){
                protocalTile.setVisible(true);
                mechanismTile.setVisible(true);
                usernameTile.setVisible(true);
                passwordTile.setVisible(true);
            }else{
                protocalTile.setVisible(false);
                mechanismTile.setVisible(false);
                usernameTile.setVisible(false);
                passwordTile.setVisible(false);

            }
        });
        securityTile.setAction(securityField);
        securityTile.setActionHandler(securityField::requestFocus);
        securityField.focusedProperty().addListener((e, o, n) -> {
            securityTile.setDescription("");
        });
        securityTile.getStyleClass().add("validate");
    }


    private void initServerField() {
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
        if(securityField.isSelected()){
            if (!StringUtils.hasText(usernameField.getText())) {
                usernameTile.setDescription("[color=red]"+SettingClient.bundle().getString("cluster.username.required")+"[/color]");
                success = false;
            }
            if (!StringUtils.hasText(passwordField.getText())) {
                passwordTile.setDescription("[color=red]"+SettingClient.bundle().getString("cluster.password.required")+"[/color]");
                success = false;
            }
        }
        if (!success) {
            return;
        }
        this.saveButton.setDisable(true);
        ClusterClient.save(this.clusterProperty);
        this.clusterController.success(this.clusterProperty,this.isAdd);
        this.parent.close();
        this.saveButton.setDisable(false);
    }

    public void set(ClusterController clusterController, Stage parent, ClusterProperty property,boolean isAdd) {
        this.clusterProperty = property.copy();
        this.nameField.setText(property.getName());
        this.serverField.setText(property.getServers());
        this.securityField.setSelected(property.getSecurity());
        this.usernameField.setText(property.getUsername());
        this.mechanismField.setValue(property.getMechanism());
        this.passwordField.setText(property.getPassword());
        this.clusterController = clusterController;
        this.parent = parent;
        this.isAdd = isAdd;
    }
}
