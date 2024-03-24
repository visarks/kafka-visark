package com.podigua.kafka.visark.home.layout;

import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.podigua.kafka.admin.task.QuerySingleConsumerTask;
import com.podigua.kafka.core.utils.NodeUtils;
import com.podigua.kafka.visark.home.control.AssignmentTableCell;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.PopupWindow;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.MemberDescription;
import org.apache.kafka.common.ConsumerGroupState;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * 显示consumer 详情
 *
 * @author podigua
 * @date 2024/03/23
 */
public class ShowConsumerDetailPane extends BaseRefreshPane {
    private final static double WIDTH = 280;
    /**
     * 组 ID
     */
    private final String groupId;
    /**
     * 根
     */
    private final AnchorPane root = new AnchorPane();

    /**
     * 是简单消费者群体
     */
    private final SimpleBooleanProperty isSimpleConsumerGroup = new SimpleBooleanProperty(false);
    /**
     * 分区分配器
     */
    private final SimpleStringProperty partitionAssignor = new SimpleStringProperty("");
    /**
     * 州
     */
    private final SimpleObjectProperty<ConsumerGroupState> state = new SimpleObjectProperty(ConsumerGroupState.UNKNOWN);
    /**
     * 协调者
     */
    private final SimpleObjectProperty<Node> coordinator = new SimpleObjectProperty(Node.noNode());
    /**
     * 加载中
     */
    private final HBox loading = new HBox(NodeUtils.progress(), new Label(SettingClient.bundle().getString("form.loading")));
    /**
     * 表视图
     */
    private final TableView<MemberDescription> tableView = new TableView<>();

    public ShowConsumerDetailPane(String clusterId, String groupId) {
        super(clusterId);
        this.groupId = groupId;
        addCenter();
        this.setPrefSize(760, 520);
        reload();
    }

    /**
     * 重新加载
     */
    protected void reload() {
        this.loading.setVisible(true);
        tableView.getItems().clear();
        QuerySingleConsumerTask task = new QuerySingleConsumerTask(clusterId(), groupId);
        task.setOnSucceeded(event -> {
            this.loading.setVisible(false);
            try {
                ConsumerGroupDescription description = task.get();
                this.isSimpleConsumerGroup.set(description.isSimpleConsumerGroup());
                this.partitionAssignor.set(description.partitionAssignor());
                this.state.set(description.state());
                this.coordinator.set(description.coordinator());
                this.tableView.getItems().clear();
                this.tableView.getItems().addAll(description.members());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        task.setOnFailed(event -> this.loading.setVisible(false));
        new Thread(task).start();
    }

    private void addCenter() {
        VBox root = new VBox();
        root.setSpacing(10);
        Pane form = addForm();
        setTableColumn();
        root.getChildren().addAll(form, getHeader(SettingClient.bundle().getString("context.menu.members")), tableView);
        NodeUtils.setAnchor(root, 0);
        loading.setAlignment(Pos.CENTER);
        NodeUtils.setAnchor(loading, 0);
        this.root.getChildren().addAll(root, loading);
        this.setCenter(this.root);
    }

    private Pane addForm() {
        HBox header = getHeader(SettingClient.bundle().getString("basic.information"));
        VBox box = new VBox(header, addGroupId(), addState(), addCoordinator());
        box.setPadding(new Insets(5));
        box.setSpacing(5);
        return box;
    }

    private static HBox getHeader(String title) {
        HBox header = new HBox();
        Label label = new Label(title);
        header.getChildren().add(label);
        label.getStyleClass().add(Styles.TITLE_4);
        header.setPadding(new Insets(5));
        header.setStyle("-fx-border-width: 0 0 1 0;-fx-border-color: -color-border-default");
        return header;
    }

    private Tile addCoordinator() {
        Tile tile = new Tile("Coordinator", "");
        Label field = getLabel(coordinator.get().host() + ":" + coordinator.get().port());
        coordinator.addListener((observable, oldValue, newValue) -> {
            field.setText(newValue.host() + ":" + newValue.port());
        });
        tile.setAction(field);
        return tile;
    }

    private Label getLabel(String value) {
        Label field = new Label(value);
        field.setPrefWidth(WIDTH);
        field.getStyleClass().add(Styles.TITLE_4);
        return field;
    }

    private Tile addState() {
        Tile tile = new Tile("State", "");
        Label field = getLabel(state.get().toString());
        state.addListener((observable, oldValue, newValue) -> {
            field.setText(newValue.toString());
        });
        tile.setAction(field);
        return tile;
    }

    private Tile addGroupId() {
        Tile tile = new Tile("GroupId", "");
        Label field = getLabel(groupId);
        tile.setAction(field);
        return tile;
    }

    private void setTableColumn() {
        TableColumn<MemberDescription, String> memberId = new TableColumn<>("MemberId");
        memberId.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().consumerId()));

        TableColumn<MemberDescription, String> clientId = new TableColumn<>("ClientId");
        clientId.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().clientId()));

        TableColumn<MemberDescription, String> host = new TableColumn<>("Host");
        host.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().host()));
        host.setPrefWidth(120);

        TableColumn<MemberDescription, Number> assignment = new TableColumn<>("Assignment");
        assignment.setCellFactory(column -> new AssignmentTableCell());
        assignment.setCellValueFactory(features -> new SimpleObjectProperty<>(features.getValue().assignment().topicPartitions().size()));
        assignment.setPrefWidth(120);

        memberId.prefWidthProperty().bind(tableView.widthProperty().subtract(host.prefWidthProperty()).subtract(assignment.prefWidthProperty()).divide(3).multiply(2));
        clientId.prefWidthProperty().bind(tableView.widthProperty().subtract(host.prefWidthProperty()).subtract(assignment.prefWidthProperty()).divide(3).subtract(10));
        tableView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE);
        tableView.getColumns().addAll(memberId, clientId, host, assignment);
    }
}
