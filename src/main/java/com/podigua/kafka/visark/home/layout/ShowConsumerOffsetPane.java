package com.podigua.kafka.visark.home.layout;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.podigua.kafka.admin.ConsumerOffset;
import com.podigua.kafka.admin.task.QueryConsumerOffsetTask;
import com.podigua.kafka.core.event.LoadingEvent;
import com.podigua.kafka.core.utils.Messages;
import com.podigua.kafka.core.utils.NodeUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.util.StringUtils;

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
     * 搜索
     */
    private final Button search = new Button();

    private final CustomTextField filter = new CustomTextField("");
    /**
     * 表视图
     */
    private final TableView<ConsumerOffset> tableView = new TableView<>();

    private final FontIcon searchIcon = new FontIcon(Material2MZ.SEARCH);

    private final ObservableList<ConsumerOffset> rows = FXCollections.observableArrayList();

    /**
     * 过滤 器
     */
    private FilteredList<ConsumerOffset> filters = new FilteredList<>(rows);

    public ShowConsumerOffsetPane(String clusterId, String groupId) {
        this.clusterId = clusterId;
        this.groupId = groupId;
        this.tableView.setItems(filters);
        addTop();
        addCenter();
        reload();
    }

    private void addTop() {
        this.filter.setPromptText(Messages.filter());
        FontIcon icon = NodeUtils.clear(() -> filter.setText(""));
        filter.setRight(icon);
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            filters.predicateProperty().set(node -> {
                if (node == null || !StringUtils.hasText(newValue)) {
                    return true;
                }
                return node.topic().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        this.filter.setPrefWidth(220);
        ToolBar toolBar = new ToolBar();
        search.setGraphic(searchIcon);
        search.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        search.setOnAction(event -> reload());
        toolBar.getItems().addAll(filter, new Separator(Orientation.VERTICAL), search);
        this.setTop(toolBar);
    }

    public void reload() {
        LoadingEvent.LOADING.publish();
        this.rows.clear();
        QueryConsumerOffsetTask task = new QueryConsumerOffsetTask(clusterId, groupId);
        task.setOnSucceeded(event -> {
            try {
                LoadingEvent.STOP.publish();
                List<ConsumerOffset> offsets = task.get();
                this.rows.addAll(offsets);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        task.setOnCancelled(event -> {
            Platform.runLater(() -> {
                throw new RuntimeException(event.getSource().getException());
            });
        });
        new Thread(task).start();
    }

    private void addCenter() {
        setTableColumn();
        this.setCenter(tableView);
    }

    private void setTableColumn() {
        TableColumn<ConsumerOffset, String> topic = new TableColumn<>("Topic");
        topic.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().topic()));

        TableColumn<ConsumerOffset, String> partition = new TableColumn<>("Partition");
        partition.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().partition() + ""));

        TableColumn<ConsumerOffset, String> start = new TableColumn<>("Start");
        start.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().start() + ""));

        TableColumn<ConsumerOffset, String> end = new TableColumn<>("End");
        end.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().end() + ""));

        TableColumn<ConsumerOffset, String> offset = new TableColumn<>("Offset");
        offset.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().offset() + ""));

        TableColumn<ConsumerOffset, String> tag = new TableColumn<>("Tag");
        tag.setCellValueFactory(param -> new SimpleStringProperty((param.getValue().end() - param.getValue().offset()) + ""));

        TableColumn<ConsumerOffset, String> host = new TableColumn<>("Host");
        host.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().host()));

        TableColumn<ConsumerOffset, String> memberId = new TableColumn<>("MemberId");
        memberId.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().memberId()));

        TableColumn<ConsumerOffset, String> clientId = new TableColumn<>("ClientId");
        clientId.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().clientId()));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);

        tableView.getColumns().addAll(topic, partition, start, end, offset, tag, host, memberId, clientId);
        tableView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE, Styles.STRIPED);

    }
}
