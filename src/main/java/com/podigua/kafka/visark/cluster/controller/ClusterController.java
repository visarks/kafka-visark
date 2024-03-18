package com.podigua.kafka.visark.cluster.controller;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 **/
public class ClusterController implements Initializable {
    public Button addButton;
    public Button deleteButton;
    public Button connectButton;
    public TableView<ClusterProperty> tableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initStyle();
        initTable();
    }

    private void initTable() {
        tableView.setItems(FXCollections.observableArrayList(new ClusterProperty(),new ClusterProperty(),new ClusterProperty(),new ClusterProperty(),new ClusterProperty(),new ClusterProperty()));
        tableView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE);


        var priority = new TableColumn<ClusterProperty, String>("#");
        priority.setCellFactory(col -> {
            var cell = new TableCell<ClusterProperty, String>();
            StringBinding value = Bindings.when(cell.emptyProperty())
                    .then("")
                    .otherwise(cell.indexProperty().add(1).asString());
            cell.textProperty().bind(value);
            return cell;
        });
        priority.setPrefWidth(45);
        priority.setResizable(false);
        priority.setSortable(false);

        priority.setCellValueFactory(property -> property.getValue().priority());
        TableColumn<ClusterProperty, String> name = new TableColumn<>(SettingClient.bundle().getString("cluster.table.name"));
        name.setCellValueFactory(property -> property.getValue().name());
        TableColumn<ClusterProperty, String> servers = new TableColumn<>(SettingClient.bundle().getString("cluster.table.servers"));
        servers.setCellValueFactory(property -> property.getValue().servers());
        tableView.getColumns().addAll(priority,name,servers);
        name.prefWidthProperty().bind(tableView.widthProperty().subtract(priority.prefWidthProperty()).divide(3));
        servers.prefWidthProperty().bind(tableView.widthProperty().subtract(priority.prefWidthProperty()).subtract(name.prefWidthProperty()).subtract(3));
    }

    private void initStyle() {
        addButton.setGraphic(new FontIcon(Material2AL.ADD));
        addButton.getStyleClass().addAll(
                Styles.FLAT,Styles.ACCENT
        );
        deleteButton.setGraphic(new FontIcon(Material2AL.DELETE));
        deleteButton.getStyleClass().addAll(
                Styles.FLAT,Styles.DANGER
        );
        connectButton.setGraphic(new FontIcon(Material2AL.CAST_CONNECTED));
        connectButton.getStyleClass().addAll(
                Styles.FLAT,Styles.ACCENT
        );
    }
}
