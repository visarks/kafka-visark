package com.podigua.kafka.visark.home.layout;

import atlantafx.base.theme.Tweaks;
import com.podigua.kafka.State;
import com.podigua.kafka.admin.Partition;
import com.podigua.kafka.admin.task.QueryPartitionTask;
import com.podigua.kafka.core.utils.AlertUtils;
import com.podigua.kafka.core.utils.NodeUtils;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * 显示分区窗格
 *
 * @author podigua
 * @date 2024/03/23
 */
public class ShowPartitionPane extends BaseRefreshPane {
    /**
     * 主题
     */
    private final String topic;
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
    private final TableView<Partition> tableView = new TableView<>();

    public ShowPartitionPane(String clusterId, String topic) {
        super(clusterId);
        this.topic = topic;
        addCenter();
        this.setPrefSize(684, 414);
        reload();
    }

    /**
     * 重新加载
     */
    protected void reload() {
        tableView.getItems().clear();
        this.loading.setVisible(true);
        QueryPartitionTask task = new QueryPartitionTask(clusterId(), topic);
        task.setOnSucceeded(event -> {
            this.loading.setVisible(false);
            try {
                List<Partition> partitions = task.get();
                tableView.getItems().addAll(partitions);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        task.setOnFailed(event -> {
            this.loading.setVisible(false);
            AlertUtils.error(State.stage(), event.getSource().getException().getMessage());
        });
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
        TableColumn<Partition, String> partition = new TableColumn<>("Partition");
        partition.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().partition() + ""));
        partition.setResizable(false);
        partition.setSortable(false);
        partition.setPrefWidth(80);

        TableColumn<Partition, String> leader = new TableColumn<>("Leader");
        leader.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().leader() + ""));
        leader.setSortable(false);

        TableColumn<Partition, String> replicas = new TableColumn<>("Replicas");
        replicas.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().replicas() + ""));
        replicas.setSortable(false);

        TableColumn<Partition, String> isr = new TableColumn<>("Isr");
        isr.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().replicas() + ""));
        isr.setSortable(false);
        leader.prefWidthProperty().bind(tableView.widthProperty().subtract(partition.prefWidthProperty()).divide(3));
        replicas.prefWidthProperty().bind(tableView.widthProperty().subtract(partition.prefWidthProperty()).divide(3));
        isr.prefWidthProperty().bind(tableView.widthProperty().subtract(partition.prefWidthProperty()).divide(3).subtract(9));

        tableView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE);
        tableView.getColumns().addAll(partition, leader, replicas, isr);
    }
}
