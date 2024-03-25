package com.podigua.kafka.visark.home.layout;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.podigua.kafka.admin.Partition;
import com.podigua.kafka.admin.QueryParams;
import com.podigua.kafka.admin.TopicOffset;
import com.podigua.kafka.admin.enums.OffsetType;
import com.podigua.kafka.admin.enums.SearchType;
import com.podigua.kafka.admin.task.QueryPartitionTask;
import com.podigua.kafka.admin.task.QueryTopicOffsetTask;
import com.podigua.kafka.admin.task.SearchMessageTask;
import com.podigua.kafka.core.event.LoadingEvent;
import com.podigua.kafka.core.utils.*;
import com.podigua.kafka.visark.home.control.DateTimePicker;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import com.podigua.kafka.visark.home.entity.Message;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static javafx.geometry.Orientation.VERTICAL;

/**
 * 消息窗格
 *
 * @author podigua
 * @date 2024/03/24
 */
public class TopicMessagePane extends BorderPane {
    /**
     * 节点
     */
    private final ClusterNode node;
    /**
     * 进展
     */
    private final ProgressIndicator progress = NodeUtils.progress();
    private final ProgressIndicator searchProgress = NodeUtils.progress();
    private final FontIcon searchIcon = new FontIcon(Material2MZ.SEARCH);

    /**
     * 分区
     */
    private final ComboBox<PartitionSelect> partitions = new ComboBox<>();

    private final Button start = new Button();
    private final Tooltip startTooltip = new Tooltip(SettingClient.bundle().getString("message.start"));
    private final Button search = new Button();
    private final Tooltip searchTooltip = new Tooltip(SettingClient.bundle().getString("message.search"));
    private final Button clear = new Button();
    private final Tooltip clearTooltip = new Tooltip(SettingClient.bundle().getString("message.clear"));
    private final Tooltip addTooltip = new Tooltip(SettingClient.bundle().getString("message.add"));
    private final Button add = new Button();

    private final CustomTextField filter = new CustomTextField();
    /**
     * 刷新
     */
    private final Button refresh = new Button();
    private final Tooltip refreshTooltip = new Tooltip(SettingClient.bundle().getString("message.refresh"));
    private final Tooltip messageTooltip = new Tooltip(SettingClient.bundle().getString("max.messages"));

    /**
     * 偏移组
     */
    private final ToggleGroup offsetGroup = new ToggleGroup();
    /**
     * 类型组
     */
    private final ToggleGroup typeGroup = new ToggleGroup();

    /**
     * 计数
     */
    private final Spinner<Integer> counts = new Spinner<>(1, 10000, 500, 500);

    private final HBox dynamic = new HBox();

    /**
     * 偏移量
     */
    private Spinner<Integer> offset = new Spinner<>(0L, 0L, 0L, 1L);


    private final DateTimePicker picker = new DateTimePicker();

    private final TableView<Message> table = new TableView<>();

    private final ObservableList<Message> rows = FXCollections.observableArrayList();

    private FilteredList<Message> filters = new FilteredList<>(rows);


    public TopicMessagePane(ClusterNode node) {
        this.node = node;
        this.getStylesheets().add(Resources.getResource("/css/main.css").toExternalForm());
        addTools();
        addAction();
        setTable();
        startTask();
        picker.setDateTimeValue(LocalDateTime.now());
    }

    private void startTask() {
        QueryTopicOffsetTask task = new QueryTopicOffsetTask(node.clusterId(), node.label());
        task.setOnSucceeded(event -> {
            try {
                int min = Integer.MAX_VALUE;
                int max = -1;
                List<TopicOffset> offsets = task.get();
                for (TopicOffset topicOffset : offsets) {
                    min = Math.min(min, topicOffset.start().intValue());
                    max = Math.max(max, topicOffset.end().intValue());
                }
                offset = new Spinner<>(min, max, max, 100);
                offset.setEditable(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        ThreadUtils.start(task);
        partitions.getItems().clear();
        QueryPartitionTask partitionTask = new QueryPartitionTask(node.clusterId(), node.label());
        partitionTask.setOnSucceeded(e -> {
            try {
                List<Partition> list = partitionTask.get();
                List<PartitionSelect> selects = new ArrayList<>();
                PartitionSelect all = new PartitionSelect(-1, SettingClient.bundle().getString("message.partitions.all"));
                selects.add(all);
                selects.addAll(list.stream().map(p ->
                        new PartitionSelect(p.partition(), p.partition() + "")
                ).toList());
                partitions.setValue(all);
                partitions.getItems().addAll(selects);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        ThreadUtils.start(partitionTask);
    }

    private void setTable() {
        this.table.setItems(filters);
        filter.setPromptText(Messages.filter());
        filter.setPrefWidth(220);
        FontIcon icon = NodeUtils.clear(() -> filter.setText(""));
        filter.setRight(icon);
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            filters.predicateProperty().set(node -> {
                if (node == null || !StringUtils.hasText(newValue)) {
                    return true;
                }
                return node.value().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        {
            TableColumn<Message, Number> partition = new TableColumn<>("Partition");
            partition.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().partition()));
            partition.setPrefWidth(200);

            TableColumn<Message, Number> offset = new TableColumn<>("Offset");
            offset.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().offset()));
            offset.setPrefWidth(200);

            TableColumn<Message, String> key = new TableColumn<>("Key");
            key.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().key()));
            key.setPrefWidth(220);


            TableColumn<Message, String> value = new TableColumn<>("Value");
            value.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().value()));

            TableColumn<Message, String> timestamp = new TableColumn<>("Timestamp");
            timestamp.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().timestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            timestamp.setPrefWidth(220);

            value.prefWidthProperty().bind(table.widthProperty().subtract(key.widthProperty()).subtract(timestamp.widthProperty()).subtract(offset.widthProperty()).subtract(partition.widthProperty()).subtract(10));

            table.getColumns().addAll(partition, offset, key, value, timestamp);
            table.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE, Styles.STRIPED);
            this.setCenter(table);
        }
    }

    private void addAction() {
        onSearch();
        onClear();
    }

    private void onClear() {
        this.clear.setOnAction(event -> {
            AlertUtils.confirm(SettingClient.bundle().getString("message.sure.clear")).ifPresent(t -> {
                this.rows.clear();
            });
        });
    }

    private void changeSearchStatus(boolean loading) {
        if (loading) {
            this.search.setGraphic(searchProgress);
            this.search.setDisable(true);
            LoadingEvent.LOADING.publish();
        } else {
            this.search.setGraphic(searchIcon);
            this.search.setDisable(false);
            LoadingEvent.STOP.publish();
        }
    }

    private void onSearch() {
        search.setOnAction(event -> {
            if (!this.rows.isEmpty()) {
                this.rows.clear();
            }
            changeSearchStatus(true);
            OffsetType offsetType = (OffsetType) offsetGroup.getSelectedToggle().getUserData();
            SearchType searchType = (SearchType) typeGroup.getSelectedToggle().getUserData();
            SearchMessageTask task = new SearchMessageTask(node.clusterId(), node.label(), new QueryParams(offsetType, searchType)
                    .partition(partitions.getValue().partition)
                    .time(picker.getDateTimeValue())
                    .offset(new BigDecimal(offset.getValue()).longValue())
                    .count(counts.getValue()),
                    record -> {
                        Message message = new Message(record);
                        this.rows.add(0, message);
                    });
            task.setOnSucceeded(e -> {
                changeSearchStatus(false);
                try {
                    MessageUtils.success(String.format(SettingClient.bundle().getString("message.search.success"), task.get()));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            task.setOnFailed(e -> {
                changeSearchStatus(false);
                Platform.runLater(() -> {
                    throw new RuntimeException(e.getSource().getException());
                });
            });
            ThreadUtils.start(task);
        });
    }

    private void addTools() {
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(5);
        ToolBar filters = new ToolBar();
        filters.getItems().addAll(filter, new Separator(VERTICAL), refresh);
        dynamic.setAlignment(Pos.CENTER_LEFT);
        ToolBar tool = new ToolBar();
        setStarted();
        setButton();
        counts.setEditable(true);
        counts.setTooltip(messageTooltip);

        RadioButton earliest = new RadioButton(OffsetType.earliest.name());
        earliest.setUserData(OffsetType.earliest);
        earliest.setToggleGroup(offsetGroup);

        RadioButton latest = new RadioButton(OffsetType.latest.name());
        latest.setSelected(true);
        latest.setUserData(OffsetType.latest);
        latest.setToggleGroup(offsetGroup);
        this.offset.setTooltip(new Tooltip("Offset"));
        partitions.setTooltip(new Tooltip("Partitions"));
        partitions.setPrefWidth(120);
        RadioButton message = new RadioButton(SearchType.messages.name());
        message.setSelected(true);
        message.setUserData(SearchType.messages);
        message.setToggleGroup(typeGroup);

        RadioButton datetime = new RadioButton(SearchType.datetime.name());
        datetime.setUserData(SearchType.datetime);
        datetime.setToggleGroup(typeGroup);

        RadioButton offset = new RadioButton(SearchType.offset.name());
        offset.setUserData(SearchType.offset);
        offset.setToggleGroup(typeGroup);

        typeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                SearchType offsetType = (SearchType) newValue.getUserData();
                dynamic.getChildren().clear();
                switch (offsetType) {
                    case datetime -> dynamic.getChildren().add(picker);
                    case offset -> dynamic.getChildren().add(this.offset);
                }
            }
        });
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        tool.getItems().addAll(start, search, clear, add, new Separator(VERTICAL), earliest, latest, new Separator(VERTICAL), message, datetime, offset, new Separator(VERTICAL), partitions, new Separator(VERTICAL), dynamic, spacer, counts);
        header.getChildren().addAll(filters, tool);
        this.setTop(header);
    }

    private void setButton() {
        search.setGraphic(searchIcon);
        search.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        search.setTooltip(searchTooltip);

        clear.setGraphic(new FontIcon(Material2MZ.REMOVE_CIRCLE_OUTLINE));
        clear.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.DANGER);
        clear.setTooltip(clearTooltip);

        add.setGraphic(new FontIcon(Material2AL.ADD_CIRCLE_OUTLINE));
        add.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        add.setTooltip(addTooltip);

        refresh.setGraphic(new FontIcon(Material2MZ.REFRESH));
        refresh.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        refresh.setTooltip(refreshTooltip);
    }

    private void setStarted() {
        start.setGraphic(new FontIcon(Material2MZ.PLAY_ARROW));
        start.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        start.setTooltip(startTooltip);
    }

    /**
     * 节点
     * <p>
     * latest
     */
    public ClusterNode node() {
        return node;
    }

    private record PartitionSelect(Integer partition, String label) {
        @Override
        public String toString() {
            return label;
        }
    }
}
