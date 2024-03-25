package com.podigua.kafka.visark.home.layout;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.podigua.kafka.admin.task.QuerySingleConsumerTask;
import com.podigua.kafka.core.event.LoadingEvent;
import com.podigua.kafka.core.utils.Messages;
import com.podigua.kafka.core.utils.NodeUtils;
import com.podigua.kafka.visark.home.control.AssignmentTableCell;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.MemberDescription;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.util.StringUtils;

/**
 * 显示consumer 详情
 *
 * @author podigua
 * @date 2024/03/23
 */
public class ShowConsumerDetailPane extends BorderPane {
    /**
     * 集群 ID
     */
    private final String clusterId;
    /**
     * 组 ID
     */
    private final String groupId;
    /**
     * 搜索
     */
    private final Button search = new Button();

    private final CustomTextField filter = new CustomTextField("");

    private final FontIcon searchIcon = new FontIcon(Material2MZ.SEARCH);

    private final ObservableList<MemberDescription> rows = FXCollections.observableArrayList();

    /**
     * 过滤 器
     */
    private FilteredList<MemberDescription> filters = new FilteredList<>(rows);
    /**
     * 表视图
     */
    private final TableView<MemberDescription> tableView = new TableView<>();

    public ShowConsumerDetailPane(String clusterId, String groupId) {
        this.clusterId = clusterId;
        this.groupId = groupId;
        this.tableView.setItems(filters);
        addTop();
        addCenter();
        reload();
    }

    /**
     * 重新加载
     */
    protected void reload() {
        LoadingEvent.LOADING.publish();
        this.rows.clear();
        QuerySingleConsumerTask task = new QuerySingleConsumerTask(clusterId, groupId);
        task.setOnSucceeded(event -> {
            LoadingEvent.STOP.publish();
            try {
                ConsumerGroupDescription description = task.get();
                this.rows.addAll(description.members());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        task.setOnFailed(event -> {
            LoadingEvent.STOP.publish();
        });
        new Thread(task).start();
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
                return node.clientId().toLowerCase().contains(newValue.toLowerCase()) ||node.host().toLowerCase().contains(newValue.toLowerCase());
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

    private void addCenter() {
        setTableColumn();
        this.setCenter(this.tableView);
    }

    private void setTableColumn() {
        TableColumn<MemberDescription, String> memberId = new TableColumn<>("MemberId");
        memberId.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().consumerId()));

        TableColumn<MemberDescription, String> clientId = new TableColumn<>("ClientId");
        clientId.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().clientId()));

        TableColumn<MemberDescription, String> host = new TableColumn<>("Host");
        host.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().host()));

        TableColumn<MemberDescription, Number> assignment = new TableColumn<>("Assignment");
        assignment.setCellFactory(column -> new AssignmentTableCell());
        assignment.setCellValueFactory(features -> new SimpleObjectProperty<>(features.getValue().assignment().topicPartitions().size()));

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE);
        tableView.getColumns().addAll(memberId, clientId, host, assignment);
    }
}
