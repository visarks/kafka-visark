package com.podigua.kafka.visark.home.layout;

import atlantafx.base.theme.Tweaks;
import com.podigua.kafka.admin.ConsumerOffset;
import com.podigua.kafka.admin.task.QueryConsumerOffsetTask;
import com.podigua.kafka.core.utils.NodeUtils;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * 显示 consumer offset
 *
 * @author podigua
 * @date 2024/03/23
 */
public class ShowConsumerOffsetPane extends BorderPane {
    /**
     * 集群 ID
     */
    private final String clusterId;
    /**
     * 主题
     */
    private final String groupId;

    /**
     * 关闭
     */
    private final Button close = NodeUtils.close();
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
    private final TableView<ConsumerOffset> tableView = new TableView<>();

    public ShowConsumerOffsetPane(String clusterId, String groupId) {
        this.clusterId = clusterId;
        this.groupId = groupId;
        addCenter();
        addBottom();
        this.setPrefSize(684, 414);
        reload();
    }

    private void reload() {
        this.loading.setVisible(true);
        this.tableView.getItems().clear();
        QueryConsumerOffsetTask task = new QueryConsumerOffsetTask(clusterId, groupId);
        task.setOnSucceeded(event -> {
            this.loading.setVisible(false);
            try {
                List<ConsumerOffset> offsets = task.get();
                tableView.getItems().addAll(offsets);
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
        TableColumn<ConsumerOffset, String> topic = new TableColumn<>("Topic");
        topic.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().topic()));
        topic.setResizable(false);
        topic.setSortable(false);

        TableColumn<ConsumerOffset, String> partition = new TableColumn<>("Partition");
        partition.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().partition() + ""));
        partition.setResizable(false);
        partition.setSortable(false);
        partition.setPrefWidth(80);

        TableColumn<ConsumerOffset, String> start = new TableColumn<>("Start");
        start.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().start() + ""));
        start.setSortable(false);

        TableColumn<ConsumerOffset, String> end = new TableColumn<>("End");
        end.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().end() + ""));
        end.setSortable(false);

        TableColumn<ConsumerOffset, String> offset = new TableColumn<>("Offset");
        offset.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().offset() + ""));
        offset.setSortable(false);

        topic.prefWidthProperty().bind(tableView.widthProperty().subtract(partition.prefWidthProperty()).divide(5).multiply(2));
        start.prefWidthProperty().bind(tableView.widthProperty().subtract(partition.prefWidthProperty()).divide(5));
        end.prefWidthProperty().bind(tableView.widthProperty().subtract(partition.prefWidthProperty()).divide(5));
        offset.prefWidthProperty().bind(tableView.widthProperty().subtract(partition.prefWidthProperty()).divide(5));

        tableView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE);
        tableView.getColumns().addAll(topic,partition, start, end,offset);
    }


    private void addBottom() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setSpacing(10);
        Button refresh = NodeUtils.refresh();
        refresh.setOnAction(event -> reload());
        box.getChildren().addAll(close, refresh);
        this.setBottom(box);
    }

    /**
     * 设置为关闭
     *
     * @param value 价值
     */
    public void setOnClose(EventHandler<ActionEvent> value) {
        close.setOnAction(value);
    }
}
