package com.podigua.kafka.visark.home.layout;

import atlantafx.base.theme.Tweaks;
import com.podigua.kafka.admin.TopicOffset;
import com.podigua.kafka.admin.task.QueryTopicOffsetTask;
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

import java.util.Comparator;
import java.util.List;

/**
 * 显示topic offset
 *
 * @author podigua
 * @date 2024/03/23
 */
public class ShowTopicOffsetPane extends BaseRefreshPane {
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
    private final TableView<TopicOffset> tableView = new TableView<>();

    public ShowTopicOffsetPane(String clusterId, String topic) {
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
        this.loading.setVisible(true);
        this.tableView.getItems().clear();
        QueryTopicOffsetTask task = new QueryTopicOffsetTask(clusterId(), topic);
        task.setOnSucceeded(event -> {
            this.loading.setVisible(false);
            try {
                List<TopicOffset> offsets = task.get();
                tableView.getItems().addAll(offsets.stream().sorted(Comparator.comparing(TopicOffset::partition)).toList());
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
        TableColumn<TopicOffset, String> partition = new TableColumn<>("Partition");
        partition.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().partition() + ""));
        partition.setPrefWidth(100);

        TableColumn<TopicOffset, String> start = new TableColumn<>("Start");
        start.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().start() + ""));

        TableColumn<TopicOffset, String> end = new TableColumn<>("End");
        end.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().end() + ""));

        TableColumn<TopicOffset, String> counts = new TableColumn<>("Messages");
        counts.setCellValueFactory(param -> new SimpleStringProperty((param.getValue().end()-param.getValue().start()) + ""));

        start.prefWidthProperty().bind(tableView.widthProperty().subtract(partition.prefWidthProperty()).divide(3));
        end.prefWidthProperty().bind(tableView.widthProperty().subtract(partition.prefWidthProperty()).divide(3));
        counts.prefWidthProperty().bind(tableView.widthProperty().subtract(partition.prefWidthProperty()).divide(3).subtract(10));

        tableView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE);
        tableView.getColumns().addAll(partition, start, end,counts);
    }
}
