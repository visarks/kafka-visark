package com.podigua.kafka.visark.home.layout;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.podigua.kafka.admin.task.QueryNodesTask;
import com.podigua.kafka.core.event.LoadingEvent;
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
        LoadingEvent.LOADING.publish();
        QueryNodesTask task = new QueryNodesTask(clusterId());
        task.setOnSucceeded(event -> {
            LoadingEvent.STOP.publish();
            try {
                tableView.getItems().addAll(task.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        task.setOnFailed(event -> {
            LoadingEvent.STOP.publish();
        });
        new Thread(task).start();
    }

    private void addCenter() {
        setTableColumn();
        this.setCenter(tableView);
    }

    private void setTableColumn() {
        TableColumn<ClusterNode, String> id = new TableColumn<>("Id");
        id.setCellValueFactory(param -> new SimpleStringProperty(((Node) param.getValue().nativeValue()).idString()));
//        id.setPrefWidth(80);
        TableColumn<ClusterNode, String> host = new TableColumn<>("Host");
        host.setCellValueFactory(param -> new SimpleStringProperty(((Node) param.getValue().nativeValue()).host()));

        TableColumn<ClusterNode, String> port = new TableColumn<>("Port");
        port.setCellValueFactory(param -> new SimpleStringProperty(((Node) param.getValue().nativeValue()).port() + ""));
//        port.setPrefWidth(120);
        TableColumn<ClusterNode, String> rack = new TableColumn<>("Rack");
        rack.setCellValueFactory(param -> new SimpleStringProperty(((Node) param.getValue().nativeValue()).rack()));
//        host.prefWidthProperty().bind(tableView.widthProperty().subtract(port.prefWidthProperty()).subtract(id.prefWidthProperty()).divide(2));
//        rack.prefWidthProperty().bind(tableView.widthProperty().subtract(port.prefWidthProperty()).subtract(id.prefWidthProperty()).divide(2).subtract(9));
        tableView.getStyleClass().addAll(Styles.DENSE, Styles.BORDER_SUBTLE, Styles.STRIPED, Tweaks.EDGE_TO_EDGE);
        tableView.getColumns().addAll(id, host, port, rack);


        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }
}
