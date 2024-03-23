package com.podigua.kafka.visark.home.layout;

import atlantafx.base.theme.Tweaks;
import com.podigua.kafka.admin.task.QueryNodesTask;
import com.podigua.kafka.core.utils.NodeUtils;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.kafka.common.Node;

/**
 * 显示群集窗格
 *
 * @author podigua
 * @date 2024/03/23
 */
public class ShowClusterPane extends BaseRefreshPane {
    /**
     * 根
     */
    private final AnchorPane root = new AnchorPane();
    /**
     * 加载中
     */
    private final HBox loading = new HBox(NodeUtils.progress(), new Label(SettingClient.bundle().getString("form.loading")));
    /**
     * 表视图
     */
    private final TableView<ClusterNode> tableView = new TableView<>();

    public ShowClusterPane(String clusterId) {
        super(clusterId);
        addCenter();
        addBottom();
        this.setPrefSize(660, 320);
        reload();
    }

    /**
     * 重新加载
     */
    protected void reload() {
        tableView.getItems().clear();
        this.loading.setVisible(true);
        QueryNodesTask task = new QueryNodesTask(clusterId());
        task.setOnSucceeded(event -> {
            this.loading.setVisible(false);
            try {
                tableView.getItems().addAll(task.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        task.setOnFailed(event -> this.loading.setVisible(false));
        new Thread(task).start();
    }

    private void addCenter() {
        VBox box = new VBox();
        setTableColumn();
        box.getChildren().add(tableView);
        NodeUtils.setAnchor(box, 0);
        loading.setSpacing(10);
        loading.setAlignment(Pos.CENTER);
        NodeUtils.setAnchor(loading, 0);
        root.getChildren().addAll(box, loading);
        this.setCenter(root);
    }

    private void setTableColumn() {
        TableColumn<ClusterNode, String> id = new TableColumn<>("Id");
        id.setCellValueFactory(param -> new SimpleStringProperty(((Node) param.getValue().nativeValue()).idString()));
        id.setPrefWidth(80);
        TableColumn<ClusterNode, String> host = new TableColumn<>("Host");
        host.setCellValueFactory(param -> new SimpleStringProperty(((Node) param.getValue().nativeValue()).host()));

        TableColumn<ClusterNode, String> port = new TableColumn<>("Port");
        port.setCellValueFactory(param -> new SimpleStringProperty(((Node) param.getValue().nativeValue()).port() + ""));
        port.setPrefWidth(120);
        TableColumn<ClusterNode, String> rack = new TableColumn<>("Rack");
        rack.setCellValueFactory(param -> new SimpleStringProperty(((Node) param.getValue().nativeValue()).rack()));
        host.prefWidthProperty().bind(tableView.widthProperty().subtract(port.prefWidthProperty()).subtract(id.prefWidthProperty()).divide(2));
        rack.prefWidthProperty().bind(tableView.widthProperty().subtract(port.prefWidthProperty()).subtract(id.prefWidthProperty()).divide(2).subtract(9));
        tableView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE);
        tableView.getColumns().addAll(id, host, port, rack);
    }
}
