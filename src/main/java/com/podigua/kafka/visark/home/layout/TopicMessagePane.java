package com.podigua.kafka.visark.home.layout;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.podigua.kafka.State;
import com.podigua.kafka.admin.Partition;
import com.podigua.kafka.admin.QueryParams;
import com.podigua.kafka.admin.TopicOffset;
import com.podigua.kafka.admin.enums.OffsetType;
import com.podigua.kafka.admin.enums.SearchType;
import com.podigua.kafka.admin.task.MessageConsumerTask;
import com.podigua.kafka.admin.task.QueryPartitionTask;
import com.podigua.kafka.admin.task.QueryTopicOffsetTask;
import com.podigua.kafka.admin.task.SearchMessageTask;
import com.podigua.kafka.core.utils.*;
import com.podigua.kafka.visark.home.control.DateTimePicker;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import com.podigua.kafka.visark.home.entity.Message;
import com.podigua.kafka.visark.home.entity.TotalDetails;
import com.podigua.kafka.visark.home.task.ExcelOutputTask;
import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.path.Paths;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.Duration;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material.Material;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static javafx.geometry.Orientation.VERTICAL;

/**
 * 消息窗格
 *
 * @author podigua
 * @date 2024/03/24
 */
public class TopicMessagePane extends ContentBorderPane {
    private final static Logger logger = LoggerFactory.getLogger(TopicMessagePane.class);
    /**
     * 节点
     */
    private final ClusterNode node;
    /**
     * 进展
     */
    private final ProgressIndicator progress = NodeUtils.progress();
    private final ProgressIndicator searchProgress = NodeUtils.progress();

    private final SimpleBooleanProperty searching = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty starting = new SimpleBooleanProperty(false);
    private final FontIcon searchIcon = new FontIcon(Material2MZ.SEARCH);

    /**
     * 运行中
     * 2 代表可以执行
     */
    private final SimpleIntegerProperty running = new SimpleIntegerProperty(0);

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
    private final Tooltip downloadTooltip = new Tooltip(SettingClient.bundle().getString("message.download"));
    private final Tooltip messageTooltip = new Tooltip(SettingClient.bundle().getString("max.messages"));

    /**
     * 偏移组
     */
    private final ToggleGroup offsetGroup = new ToggleGroup();
    /**
     * 类型组
     */
    private final ToggleGroup typeGroup = new ToggleGroup();

    private final TotalDetails totalDetails = new TotalDetails();

    /**
     * 计数
     */
    private final Spinner<Integer> counts = new Spinner<>(1, 20000, 100, 100);

    private final HBox dynamic = new HBox();

    /**
     * 偏移量
     */
    private Spinner<Integer> offset = new Spinner<>(0L, 0L, 0L, 1L);


    private final DateTimePicker picker = new DateTimePicker();
    private ExcelOutputTask downloadTask;
    private final Button download = new Button();

    private final SimpleBooleanProperty downloading = new SimpleBooleanProperty(false);
    private final FontIcon downloadIcon = new FontIcon(Material.FILE_DOWNLOAD);
    private final FontIcon downloadStopIcon = new FontIcon(Material2MZ.STOP);

    private final TableView<Message> table = new TableView<>();

    private final ObservableList<Message> rows = FXCollections.observableArrayList();

    private final FilteredList<Message> filters = new FilteredList<>(rows, p -> true);

    private SearchMessageTask searchTask;
    private MessageConsumerTask consumerTask;

    public TopicMessagePane(ClusterNode node) {
        this.node = node;
        this.getStylesheets().add(Resources.getResource("/css/main.css").toExternalForm());
        addTools();
        addAction();
        setTable();
//        addBottom();
        refreshTask();
        setMessages();
        picker.setDateTimeValue(LocalDateTime.now());
    }

    private void setMessages() {
    }

    private void addBottom() {
        HBox box = new HBox();
        box.setVisible(true);
        box.setSpacing(10);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER_RIGHT);
        Label label = new Label(SettingClient.bundle().getString("message.total.count"));
        Label count = new Label("0");
        count.setStyle(Styles.SUCCESS);
        count.setMinWidth(30);

        box.getChildren().addAll(label, count);
        this.setBottom(box);
    }

    private void refreshTask() {
        this.running.set(0);
        this.totalDetails.clear();
        QueryTopicOffsetTask task = new QueryTopicOffsetTask(node.clusterId(), node.label());
        task.setOnSucceeded(event -> {
            try {
                int min = Integer.MAX_VALUE;
                int max = -1;
                List<TopicOffset> offsets = task.get();
                long startOffset = Long.MAX_VALUE;
                long endOffset = 0;
                long total = 0L;
                for (TopicOffset topicOffset : offsets) {
                    startOffset = Math.min(startOffset, topicOffset.start());
                    endOffset = Math.max(endOffset, topicOffset.end());
                    total += (topicOffset.end() - topicOffset.start());
                    min = Math.min(min, topicOffset.start().intValue());
                    max = Math.max(max, topicOffset.end().intValue());
                }
                this.totalDetails.offStart().set(startOffset);
                this.totalDetails.offEnd().set(endOffset);
                this.totalDetails.messages().set(total);
                offset = new Spinner<>(min, max, max, 100);
                offset.setEditable(true);
                this.running.set(this.running.get() + 1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        ThreadUtils.start(task);
        partitions.getItems().clear();
        partitions.setValue(null);
        QueryPartitionTask partitionTask = new QueryPartitionTask(node.clusterId(), node.label());
        partitionTask.setOnSucceeded(e -> {
            try {
                List<Partition> list = partitionTask.get();
                this.totalDetails.partitions().set(list.size());
                List<PartitionSelect> selects = new ArrayList<>();
                PartitionSelect all = new PartitionSelect(-1, SettingClient.bundle().getString("message.partitions.all"));
                selects.add(all);
                selects.addAll(list.stream().map(p -> new PartitionSelect(p.partition(), p.partition() + "")).toList());
                partitions.setValue(all);
                partitions.getItems().addAll(selects);
                this.running.set(this.running.get() + 1);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        ThreadUtils.start(partitionTask);
    }

    private final Object lock = new Object();

    private void setTable() {
        this.table.setItems(filters);
        filter.setPromptText(Messages.filter());
        filter.setPrefWidth(220);
        FontIcon icon = NodeUtils.clear(() -> filter.setText(""));
        filter.setRight(icon);
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            synchronized (lock) {
                Platform.runLater(() -> {
                    try {
                        this.filters.predicateProperty().set(node -> {
                            if (node == null || !StringUtils.hasText(newValue)) {
                                return true;
                            }
                            return node.value().get().toLowerCase().contains(newValue.toLowerCase());
                        });
                    } catch (Exception e) {
                        logger.warn("设置过滤器出错", e);
                    }
                });
            }
        });
        TableColumn<Message, String> priority = new TableColumn<>("#");
        priority.setCellFactory(col -> {
            var cell = new TableCell<Message, String>();
            StringBinding value = Bindings.when(cell.emptyProperty()).then("").otherwise(cell.indexProperty().add(1).asString());
            cell.textProperty().bind(value);
            return cell;
        });
        priority.setPrefWidth(60);
        priority.setSortable(false);

        TableColumn<Message, Number> partition = new TableColumn<>("Partition");
        partition.setCellValueFactory(param -> param.getValue().partition());
        partition.setPrefWidth(200);
        partition.setSortable(false);

        TableColumn<Message, Number> offset = new TableColumn<>("Offset");
        offset.setCellValueFactory(param -> param.getValue().offset());
        offset.setPrefWidth(200);
        offset.setSortable(false);

        TableColumn<Message, String> key = new TableColumn<>("Key");
        key.setCellValueFactory(param -> param.getValue().key());
        key.setPrefWidth(220);
        key.setSortable(false);


        TableColumn<Message, String> value = new TableColumn<>("Value");
        value.setCellValueFactory(param -> param.getValue().value());
        value.setSortable(false);
        value.setSortable(false);


        TableColumn<Message, String> timestamp = new TableColumn<>("Timestamp");
        timestamp.setCellValueFactory(param -> param.getValue().timestamp());
        timestamp.setPrefWidth(220);
        value.prefWidthProperty().bind(table.widthProperty().subtract(priority.prefWidthProperty()).subtract(key.widthProperty()).subtract(timestamp.widthProperty()).subtract(offset.widthProperty()).subtract(partition.widthProperty()).subtract(10));
        table.getColumns().addAll(priority, partition, offset, key, value, timestamp);
        table.setRowFactory(tv -> {
            TableRow<Message> row = new TableRow<>();
            row.setPrefHeight(40);
            return row;
        });
        table.setOnMouseClicked(event -> {
            if (MouseButton.PRIMARY.equals(event.getButton()) && event.getClickCount() == 2) {
                if (table.getSelectionModel().getSelectedItem() != null) {
                    Message message = table.getSelectionModel().getSelectedItem();
                    if (message != null) {
                        MessageDetailPane pane = new MessageDetailPane(message);
                        StageUtils.show(pane, "Message", Modality.NONE);
                    }
                }
            }
        });
        table.getStyleClass().addAll(Styles.DENSE, Styles.BORDER_SUBTLE, Styles.STRIPED, Tweaks.EDGE_TO_EDGE);

        TableUtils.sortPolicyProperty(this.table, filters, Message::sort);
        this.setCenter(table);
    }


    private void addAction() {
        onSearch();
        onClear();
        onStart();
        onDownload();
    }

    private void onDownload() {
        downloading.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                download.setGraphic(downloadStopIcon);
            } else {
                download.setGraphic(downloadIcon);
            }
        });
        Button openFolder = new Button(SettingClient.bundle().getString("open.folder"));
        openFolder.getStyleClass().addAll(Styles.FONT_ICON, Styles.FLAT);
        openFolder.setGraphic(new FontIcon(Material.FOLDER_OPEN));
        Button openFile = new Button(SettingClient.bundle().getString("open.file"));
        openFile.getStyleClass().addAll(Styles.FONT_ICON, Styles.FLAT);
        openFile.setGraphic(new FontIcon(AntDesignIconsOutlined.FILE_EXCEL));
        download.setOnAction(event -> {
            if (CollectionUtils.isEmpty(rows)) {
                MessageUtils.warning(SettingClient.bundle().getString("data.empty"));
                return;
            }
            String folder = SettingClient.get().getDownloadFolder();
            File target = null;
            String filename = this.value().label() + ".xlsx";
            if (StringUtils.hasText(folder) && new File(folder).exists()) {
                target = FileUtils.guess(new File(folder), filename);
            } else {
                FileChooser chooser = new FileChooser();
                chooser.setInitialDirectory(new File(Paths.downloads()));
                chooser.setInitialFileName(filename);
                target = chooser.showSaveDialog(State.stage());
            }
            if (target != null) {
                downloading.setValue(!downloading.get());
                if (downloading.get()) {
                    downloadTask = new ExcelOutputTask(target, new ArrayList<>(rows));
                    File finalTarget = target;
                    downloadTask.setOnSucceeded(e -> {
                        downloading.setValue(false);
                        Boolean success = (Boolean) e.getSource().getValue();
                        if (success) {
                            openFolder.setOnAction(e1 -> Desktop.getDesktop().browseFileDirectory(finalTarget));
                            openFile.setOnAction(e1 -> {
                                try {
                                    Desktop.getDesktop().open(finalTarget);
                                } catch (IOException ex) {
                                    logger.error("打开文件失败",e);
                                    throw new RuntimeException(ex);
                                }
                            });
                            MessageUtils.success(SettingClient.bundle().getString("download.success"), Duration.seconds(5), openFolder, openFile);
                        } else {
                            MessageUtils.success(SettingClient.bundle().getString("download.cancel"));
                        }
                    });
                    downloadTask.setOnFailed(handler -> {
                        downloading.setValue(false);
                        Throwable exception = handler.getSource().getException();
                        logger.error("下载失败", exception);
                        MessageUtils.error(SettingClient.bundle().getString("download.fail"));
                        throw new RuntimeException(exception);
                    });
                    new Thread(downloadTask).start();
                } else {
                    if (downloadTask != null && downloadTask.isRunning()) {
                        downloadTask.shutdown();
                    }
                }
            }
        });
    }

    private void onStart() {
        this.starting.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                FontIcon fontIcon = new FontIcon(Material2MZ.STOP);
                fontIcon.getStyleClass().add(Styles.DANGER);
                start.setGraphic(fontIcon);
                search.setDisable(true);
                filter.setDisable(true);
                clear.setDisable(true);
            } else {
                start.setGraphic(new FontIcon(Material2MZ.PLAY_ARROW));
                search.setDisable(false);
                filter.setDisable(false);
                clear.setDisable(false);
            }
        });
        start.setOnAction(event -> {
            if (isRefreshConfig()) {
                MessageUtils.warning(SettingClient.bundle().getString("message.config.initial"));
                return;
            }
            if (starting.get()) {
                if (consumerTask != null && consumerTask.isRunning()) {
                    consumerTask.shutdown();
                    this.starting.set(false);
                    return;
                }
            }
            this.rows.clear();
            this.starting.set(true);
            OffsetType offsetType = (OffsetType) offsetGroup.getSelectedToggle().getUserData();
            var messageCounts = new AtomicLong(0);
            consumerTask = new MessageConsumerTask(node.clusterId(), node.label(), offsetType.name(), record -> {
                var message = new Message(record);
                synchronized (lock) {
                    Platform.runLater(() -> this.rows.addFirst(message));
                }
                messageCounts.getAndIncrement();
            });
            long start = System.currentTimeMillis();
            consumerTask.setOnSucceeded(e -> {
                this.starting.set(false);
                if (consumerTask.isShutdown()) {
                    MessageUtils.success(String.format(SettingClient.bundle().getString("message.search.cancel"), messageCounts.get(), System.currentTimeMillis() - start));
                } else {
                    MessageUtils.success(String.format(SettingClient.bundle().getString("message.search.success"), messageCounts.get(), System.currentTimeMillis() - start));
                }
            });
            consumerTask.setOnFailed(e -> {
                this.starting.set(false);
                Platform.runLater(() -> {
                    throw new RuntimeException(e.getSource().getException());
                });
            });
            ThreadUtils.start(consumerTask);
        });
    }

    private void onClear() {
        this.clear.setOnAction(event -> {
            AlertUtils.confirm(SettingClient.bundle().getString("message.sure.clear")).ifPresent(t -> {
                this.rows.clear();
            });
        });
    }

    private boolean isRefreshConfig() {
        return this.running.get() != 2;
    }

    private void onSearch() {
        searching.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                FontIcon fontIcon = new FontIcon(Material2MZ.STOP);
                fontIcon.getStyleClass().add(Styles.DANGER);
                search.setGraphic(fontIcon);
                start.setDisable(true);
                filter.setDisable(true);
                clear.setDisable(true);
            } else {
                search.setGraphic(searchIcon);
                start.setDisable(false);
                filter.setDisable(false);
                clear.setDisable(false);
            }
        });
        search.setOnAction(event -> {
            if (isRefreshConfig()) {
                MessageUtils.warning(SettingClient.bundle().getString("message.config.initial"));
                return;
            }
            if (searching.get()) {
                if (searchTask != null && searchTask.isRunning()) {
                    searchTask.shutdown();
                    this.searching.set(false);
                    return;
                }
            }
            this.rows.clear();
            this.searching.set(true);
            OffsetType offsetType = (OffsetType) offsetGroup.getSelectedToggle().getUserData();
            SearchType searchType = (SearchType) typeGroup.getSelectedToggle().getUserData();
            var messageCounts = new AtomicLong(0);
            searchTask = new SearchMessageTask(node.clusterId(), node.label(), new QueryParams(offsetType, searchType).partition(partitions.getValue().partition).time(picker.getDateTimeValue()).offset(new BigDecimal(offset.getValue()).longValue()).count(counts.getValue()), record -> {
                var message = new Message(record);
                this.rows.add(0, message);
                messageCounts.getAndIncrement();
            });
            long start = System.currentTimeMillis();
            searchTask.setOnSucceeded(e -> {
                this.searching.set(false);
                if (searchTask.isShutdown()) {
                    MessageUtils.success(String.format(SettingClient.bundle().getString("message.search.cancel"), messageCounts.get(), System.currentTimeMillis() - start));
                } else {
                    MessageUtils.success(String.format(SettingClient.bundle().getString("message.search.success"), messageCounts.get(), System.currentTimeMillis() - start));
                }
            });
            searchTask.setOnFailed(e -> {
                this.searching.set(false);
                Platform.runLater(() -> {
                    throw new RuntimeException(e.getSource().getException());
                });
            });
            ThreadUtils.start(searchTask);
        });
    }

    private void addTools() {
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(5);
        ToolBar filters = setFilterToolBar();
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
        setAddMessagePane();
        tool.getItems().addAll(start, search, clear, add, new Separator(VERTICAL), earliest, latest, new Separator(VERTICAL), message, datetime, offset, new Separator(VERTICAL), partitions, new Separator(VERTICAL), dynamic, download, spacer, counts);
        header.getChildren().addAll(filters, tool);
        this.setTop(header);
    }

    private void setAddMessagePane() {
        this.add.setOnAction((event) -> {
            AddMessagePane pane = new AddMessagePane(this.node.clusterId(), this.node.label());
            StageUtils.show(pane, SettingClient.bundle().getString("message.sender"), Modality.NONE);
        });
    }

    private ToolBar setFilterToolBar() {
        ToolBar filters = new ToolBar();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox details = new HBox();
        details.setPadding(new Insets(0, 20, 0, 0));
        details.setSpacing(15);
        details.setAlignment(Pos.CENTER_RIGHT);
        Label partition = new Label("0");
        partition.getStyleClass().add(Styles.SUCCESS);
        this.totalDetails.partitions().addListener((observableValue, oldValue, newValue) -> {
            partition.setText(newValue.longValue() + "");
        });

        Label offset = new Label("0-0");
        offset.getStyleClass().add(Styles.SUCCESS);
        this.totalDetails.offStart().addListener((observableValue, oldValue, newValue) -> {
            offset.setText(newValue.longValue() + "-" + this.totalDetails.offEnd().get());
        });
        this.totalDetails.offEnd().addListener((observableValue, oldValue, newValue) -> {
            offset.setText(this.totalDetails.offStart().get() + "-" + newValue.longValue());
        });
        Label message = new Label("0");
        message.getStyleClass().add(Styles.SUCCESS);
        this.totalDetails.messages().addListener((observableValue, oldValue, newValue) -> {
            message.setText(newValue.longValue() + "");
        });
        details.getChildren().addAll(new Label("Partition:"), partition, new Label("Offset:"), offset, new Label("Messages:"), message);
        this.refresh.setOnAction(event -> {
            refreshTask();
        });
        filters.getItems().addAll(filter, new Separator(VERTICAL), refresh, spacer, details);
        return filters;
    }

    private void setButton() {
        search.setGraphic(searchIcon);
        search.getStyleClass().addAll(Styles.FONT_ICON, Styles.FLAT, Styles.ACCENT);
        search.setTooltip(searchTooltip);

        clear.setGraphic(new FontIcon(Material2MZ.REMOVE_CIRCLE_OUTLINE));
        clear.getStyleClass().addAll(Styles.FONT_ICON, Styles.FLAT, Styles.DANGER);
        clear.setTooltip(clearTooltip);

        add.setGraphic(new FontIcon(Material2AL.ADD_CIRCLE_OUTLINE));
        add.getStyleClass().addAll(Styles.FONT_ICON, Styles.FLAT, Styles.ACCENT);
        add.setTooltip(addTooltip);

        refresh.setGraphic(new FontIcon(Material2MZ.REFRESH));
        refresh.getStyleClass().addAll(Styles.FONT_ICON, Styles.FLAT, Styles.ACCENT);
        refresh.setTooltip(refreshTooltip);

        download.getStyleClass().addAll(Styles.FONT_ICON, Styles.FLAT, Styles.ACCENT);
        download.setTooltip(downloadTooltip);
        download.setGraphic(downloadIcon);
    }

    private void setStarted() {
        start.setGraphic(new FontIcon(Material2MZ.PLAY_ARROW));
        start.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        start.setTooltip(startTooltip);
    }

    @Override
    public ClusterNode value() {
        return node;
    }

    @Override
    public void close() {
        if (starting.get()) {
            if (consumerTask != null && consumerTask.isRunning()) {
                consumerTask.shutdown();
                this.starting.set(false);
            }
        }
    }

    private record PartitionSelect(Integer partition, String label) {
        @Override
        public String toString() {
            return label;
        }
    }
}
