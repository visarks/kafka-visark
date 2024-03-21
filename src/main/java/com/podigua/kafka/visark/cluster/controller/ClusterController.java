package com.podigua.kafka.visark.cluster.controller;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.podigua.kafka.admin.Admin;
import com.podigua.kafka.admin.AdminConnectTask;
import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.core.utils.AlertUtils;
import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.core.utils.StageUtils;
import com.podigua.kafka.visark.cluster.ClusterClient;
import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import com.podigua.kafka.visark.cluster.layout.ConnectPane;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * 集群控制器
 *
 * @author podigua
 * @date 2024/03/21
 */
public class ClusterController implements Initializable {
    private final static Logger logger = Logger.getLogger(ClusterController.class.getName());
    public Button addButton;
    public Button deleteButton;
    public Button connectButton;
    public TableView<ClusterProperty> tableView;
    public Button editButton;
    private Stage formStage;
    private Stage parentStage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initStyle();
        initTable();
    }

    private void initTable() {
        ObservableList<ClusterProperty> items = FXCollections.observableArrayList();
        tableView.setItems(items);
        reload();
        tableView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE);
        var priority = new TableColumn<ClusterProperty, String>("#");
        priority.setCellFactory(col -> {
            var cell = new TableCell<ClusterProperty, String>();
            StringBinding value = Bindings.when(cell.emptyProperty()).then("").otherwise(cell.indexProperty().add(1).asString());
            cell.textProperty().bind(value);
            return cell;
        });
        priority.setPrefWidth(45);
        priority.setResizable(false);
        priority.setSortable(false);
        TableColumn<ClusterProperty, String> name = new TableColumn<>(SettingClient.bundle().getString("cluster.table.name"));
        name.setCellValueFactory(property -> property.getValue().name());
        TableColumn<ClusterProperty, String> servers = new TableColumn<>(SettingClient.bundle().getString("cluster.table.servers"));
        servers.setCellValueFactory(property -> property.getValue().servers());
        tableView.getColumns().addAll(priority, name, servers);
        name.prefWidthProperty().bind(tableView.widthProperty().subtract(priority.prefWidthProperty()).divide(3));
        servers.prefWidthProperty().bind(tableView.widthProperty().subtract(priority.prefWidthProperty()).subtract(name.prefWidthProperty()).subtract(3));
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editButton.setDisable(false);
                connectButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                editButton.setDisable(true);
                connectButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
    }

    public void reload() {
        List<ClusterProperty> clusters = ClusterClient.query4List();
        tableView.getItems().clear();
        tableView.getItems().addAll(clusters);
        tableView.refresh();
    }

    private void initStyle() {
        addButton.setGraphic(new FontIcon(Material2AL.ADD));
        addButton.getStyleClass().addAll(Styles.FLAT, Styles.ACCENT);
        editButton.setGraphic(new FontIcon(Material2AL.EDIT));
        editButton.getStyleClass().addAll(Styles.FLAT, Styles.ACCENT);
        deleteButton.setGraphic(new FontIcon(Material2AL.DELETE));
        deleteButton.getStyleClass().addAll(Styles.FLAT, Styles.DANGER);
        connectButton.setGraphic(new FontIcon(Material2AL.CAST_CONNECTED));
        connectButton.getStyleClass().addAll(Styles.FLAT, Styles.ACCENT);
    }

    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }

    public void onAdd(ActionEvent event) {
        openForm(new ClusterProperty(), true);
    }

    private void openForm(ClusterProperty property, boolean isAdd) {
        FXMLLoader loader = Resources.getLoader("/fxml/cluster-form.fxml");
        ClusterFormController controller = loader.getController();
        String title = isAdd ? SettingClient.bundle().getString("form.new") : SettingClient.bundle().getString("form.edit");
        formStage = StageUtils.show(loader.getRoot(), title, parentStage);
        controller.set(this, formStage, property);
    }

    public void onEdit(ActionEvent event) {
        ClusterProperty property = tableView.getSelectionModel().getSelectedItem();
        openForm(property, false);
    }

    public void onDelete(ActionEvent event) {
        AlertUtils.confirm(SettingClient.bundle().getString("alert.delete.prompt")).ifPresent(type -> {
            ClusterProperty property = tableView.getSelectionModel().getSelectedItem();
            ClusterClient.deleteById(property.getId());
            reload();
        });
    }

    public void onConnect(ActionEvent event) {
        ClusterProperty property = tableView.getSelectionModel().getSelectedItem();
        AdminConnectTask task = new AdminConnectTask(new Admin(property.getServers()).timeout(SettingClient.get().getTimeout()));
        ConnectPane pane = new ConnectPane(e -> task.cancel());
        Stage stage = StageUtils.body(pane, parentStage);
        task.setOnSucceeded(e -> {
            logger.info("连接成功:" + property.getServers());
            try {
                AdminManger.put(property.getId(),task.get());
                KafkaAdminClient client = AdminManger.get(property.getId());
                System.out.println("ccc:"+client);
            } catch (Exception ex) {
                Platform.runLater(()->{
                    throw new RuntimeException(ex);
                });
            }
            stage.close();
            parentStage.close();
        });
        task.setOnFailed(e -> {
            logger.warning("连接失败:" + property.getServers());
            stage.close();
            Throwable translate = AdminManger.translate(e.getSource().getException());
            AlertUtils.error(parentStage, translate.getMessage());
        });
        task.setOnCancelled(e -> {
            logger.warning("取消连接:" + property.getServers());
            stage.close();
        });
        new Thread(task).start();
    }
}
