package com.podigua.kafka.visark.home.control;

import atlantafx.base.theme.Styles;
import com.podigua.kafka.State;
import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.admin.executor.ConsumerExistExecutor;
import com.podigua.kafka.admin.executor.GetPartitionTaskExecutor;
import com.podigua.kafka.admin.executor.TopicExistExecutor;
import com.podigua.kafka.admin.task.DeleteConsumerTask;
import com.podigua.kafka.admin.task.DeleteTopicTask;
import com.podigua.kafka.admin.task.QueryConsumersTask;
import com.podigua.kafka.admin.task.QueryTopicsTask;
import com.podigua.kafka.core.utils.AlertUtils;
import com.podigua.kafka.core.utils.ClipboardUtils;
import com.podigua.kafka.core.utils.MessageUtils;
import com.podigua.kafka.core.utils.StageUtils;
import com.podigua.kafka.visark.cluster.event.ClusterCloseEvent;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import com.podigua.kafka.visark.home.event.ClusterPublishEvent;
import com.podigua.kafka.visark.home.layout.*;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

/**
 * 群集节点更改侦听器
 *
 * @author podigua
 * @date 2024/03/22
 */
public class ClusterNodeChangeListener implements ChangeListener<TreeItem<ClusterNode>> {
    private final TreeView<ClusterNode> treeView;
    private final TabPane tabPane;

    /**
     * 断开
     */
    private final MenuItem disconnect;
    /**
     * 节点详情
     */
    private final MenuItem showCluster;
    /**
     * consumer详情
     */
    private final MenuItem showConsumer;
    /**
     * 添加主题
     */
    private final MenuItem addTopic;
    /**
     * 刷新主题
     */
    private final MenuItem refreshTopic;
    /**
     * 删除主题
     */
    private final MenuItem showPartition;
    /**
     * 删除主题
     */
    private final MenuItem deleteTopic;
    /**
     * 删除消费者
     */
    private final MenuItem deleteConsumer;
    /**
     * 偏移主题
     */
    private final MenuItem offsetTopic;
    /**
     * 添加分区
     */
    private final MenuItem addPartition;
    /**
     * 刷新消费者
     */
    private final MenuItem refreshConsumer;
    /**
     * 复制
     */
    private final MenuItem copy;
    /**
     * 分配器
     */
    private final SeparatorMenuItem splitter = new SeparatorMenuItem();
    private final SeparatorMenuItem splitter1 = new SeparatorMenuItem();


    public ClusterNodeChangeListener(TreeView<ClusterNode> treeView, TabPane tabPane) {
        this.treeView = treeView;
        this.tabPane = tabPane;
        FontIcon close = new FontIcon(Material2OutlinedAL.CLOSE);
        close.getStyleClass().add(Styles.DANGER);
        this.disconnect = new MenuItem(SettingClient.bundle().getString("context.menu.disconnect"), close);
        this.showCluster = new MenuItem(SettingClient.bundle().getString("context.menu.show.details"), new FontIcon(Material2MZ.REMOVE_RED_EYE));
        this.showConsumer = new MenuItem(SettingClient.bundle().getString("context.menu.show.details"), new FontIcon(Material2MZ.REMOVE_RED_EYE));
        this.addTopic = new MenuItem(SettingClient.bundle().getString("context.menu.create"), new FontIcon(Material2AL.ADD));
        this.refreshTopic = new MenuItem(SettingClient.bundle().getString("refresh"), new FontIcon(Material2MZ.REFRESH));
        this.offsetTopic = new MenuItem(SettingClient.bundle().getString("context.menu.offset"), new FontIcon(Material2MZ.OUTLINED_FLAG));
        this.addPartition = new MenuItem(SettingClient.bundle().getString("context.menu.add.partition"), new FontIcon(Material2AL.ADD));
        FontIcon delete = new FontIcon(Material2AL.DELETE);
        delete.getStyleClass().add(Styles.DANGER);
        this.deleteTopic = new MenuItem(SettingClient.bundle().getString("context.menu.delete"), delete);
        this.deleteConsumer = new MenuItem(SettingClient.bundle().getString("context.menu.delete"), delete);
        this.showPartition = new MenuItem(SettingClient.bundle().getString("context.menu.show.partitions"), new FontIcon(Material2MZ.REMOVE_RED_EYE));
        this.refreshConsumer = new MenuItem(SettingClient.bundle().getString("refresh"), new FontIcon(Material2MZ.REFRESH));
        this.copy = new MenuItem(SettingClient.bundle().getString("context.menu.copy"), new FontIcon(AntDesignIconsOutlined.COPY));
        addAction();
    }

    private void addAction() {
        addDisconnect();
        this.copy.setOnAction(event -> selected(ClusterNodeChangeListener::copy));
        addRefreshTopicAction();
        this.deleteTopic.setOnAction(event -> checkTopicExists(ClusterNodeChangeListener::executeDeleteTopic));
        deleteConsumerAction();
        addTopicAction();
        this.showPartition.setOnAction(event -> checkTopicExists(ClusterNodeChangeListener::showPartition));
        showClusterAction();
        this.showConsumer.setOnAction(event -> selected((item, value) -> {
            new ClusterPublishEvent(value).publish();
        }));
        this.addPartition.setOnAction(event -> checkTopicExists((item, value) -> getCurrentPartitions((current) -> addPartition(value, current))));
        addRefreshConsumerAction();
        this.offsetTopic.setOnAction(event -> checkTopicExists(ClusterNodeChangeListener::topicOffset));
    }

    private void addDisconnect() {
        this.disconnect.setOnAction(event -> {
            selected((item, value) -> {
                value.loading(true);
                AdminManger.remove(value.clusterId());
                new ClusterCloseEvent(value.clusterId()).publish();
                TreeItem<ClusterNode> root = this.treeView.getRoot();
                ObservableList<TreeItem<ClusterNode>> children = root.getChildren();
                for (TreeItem<ClusterNode> child : children) {
                    if (child.getValue() != null && child.getValue().id().equals(value.id())) {
                        children.remove(child);
                        clearTabs(value);
                        break;
                    }
                }

            });
        });
    }

    private void clearTabs(ClusterNode value) {
        ObservableList<Tab> tabs = this.tabPane.getTabs();
        Iterator<Tab> iterator = tabs.iterator();
        while (iterator.hasNext()){
            Tab tab = iterator.next();
            ContentBorderPane pane = (ContentBorderPane) tab.getUserData();
            if (pane.value().clusterId().equals(value.clusterId())) {
                pane.close();
                iterator.remove();
            }
        }
    }

    private void deleteConsumerAction() {
        this.deleteConsumer.setOnAction(event -> {
            selected((item, value) -> {
                AlertUtils.confirm(SettingClient.bundle().getString("alert.delete.prompt")).ifPresent(type -> {
                    DeleteConsumerTask task = new DeleteConsumerTask(value.clusterId(), value.label());
                    task.setOnSucceeded(e -> {
                        FilterableTreeItem<ClusterNode> parent = (FilterableTreeItem<ClusterNode>) item.getParent();
                        if (parent != null) {
                            parent.getSourceChildren().remove(item);
                        }
                        MessageUtils.success(SettingClient.bundle().getString("form.delete.success"));
                    });
                    task.setOnFailed(e -> {
                        AlertUtils.error(State.stage(), e.getSource().getException().getMessage());
                    });
                    new Thread(task).start();
                });
            });
        });
    }


    private static void topicOffset(FilterableTreeItem<ClusterNode> item, ClusterNode value) {
        ShowTopicOffsetPane pane = new ShowTopicOffsetPane(value.clusterId(), value.label());
        Stage stage = StageUtils.show(pane, SettingClient.bundle().getString("context.menu.offset"), Modality.NONE);
        pane.setOnClose(e -> stage.close());
    }

    /**
     * 检查消费者是否存在
     *
     * @param consumer 消费者
     */
    private void checkConsumerExists(BiConsumer<FilterableTreeItem<ClusterNode>, ClusterNode> consumer) {
        selected((item, value) -> {
            value.loading(true);
            this.treeView.refresh();
            ConsumerExistExecutor executor = new ConsumerExistExecutor(value.clusterId(), value.label());
            executor.success(bool -> {
                value.loading(false);
                this.treeView.refresh();
                consumer.accept(item, value);
            });
            executor.fail(throwable -> {
                AlertUtils.error(State.stage(), SettingClient.bundle().getString("error.consumer.not.exist"));
                value.loading(true);
                treeView.refresh();
                if (item.getParent() != null) {
                    ((FilterableTreeItem<ClusterNode>) item.getParent()).getSourceChildren().remove(item);
                }
            });
            executor.execute();
        });
    }

    private void showClusterAction() {
        this.showCluster.setOnAction(event -> {
            selected((item, value) -> {
                ShowClusterPane pane = new ShowClusterPane(value.clusterId());
                Stage stage = StageUtils.show(pane, SettingClient.bundle().getString("cluster.detail.title"), Modality.NONE);
                pane.setOnClose(e -> stage.close());
            });
        });
    }

    /**
     * 选择
     *
     * @param consumer 消费者
     */
    private void selected(BiConsumer<FilterableTreeItem<ClusterNode>, ClusterNode> consumer) {
        item().ifPresent(item -> ofNullable(item.getValue()).ifPresent(value -> {
            consumer.accept(item, value);
        }));
    }

    private static void copy(FilterableTreeItem<ClusterNode> item, ClusterNode value) {
        ClipboardUtils.copy(value.label());
        MessageUtils.success(SettingClient.bundle().getString("copy.success"));
    }

    /**
     * 添加分区操作
     *
     * @param value 价值
     * @param size  大小
     */
    private static void addPartition(ClusterNode value, Integer size) {
        AddPartitionPane pane = new AddPartitionPane(value.clusterId(), value.label(), size);
        Stage stage = StageUtils.show(pane, value.label(), Modality.NONE);
        pane.setOnCancel(e1 -> stage.close());
        pane.setOnSave(success -> {
            stage.close();
            MessageUtils.success(SettingClient.bundle().getString("form.add.partition.success"));
        }, fail -> AlertUtils.error(stage, fail.getMessage()));
    }

    private static void showPartition(FilterableTreeItem<ClusterNode> item, ClusterNode value) {
        ShowPartitionPane pane = new ShowPartitionPane(value.clusterId(), value.label());
        Stage stage = StageUtils.show(pane, value.label(), Modality.NONE);
        pane.setOnClose(e -> stage.close());
    }

    private void checkTopicExists(BiConsumer<FilterableTreeItem<ClusterNode>, ClusterNode> consumer) {
        selected((item, value) -> {
            value.loading(true);
            this.treeView.refresh();
            TopicExistExecutor executor = new TopicExistExecutor(value.clusterId(), value.label());
            executor.success(bool -> {
                value.loading(false);
                this.treeView.refresh();
                consumer.accept(item, value);
            });
            executor.fail(throwable -> {
                AlertUtils.error(State.stage(), SettingClient.bundle().getString("error.topic.not.exist"));
                value.loading(true);
                treeView.refresh();
                if (item.getParent() != null) {
                    ((FilterableTreeItem<ClusterNode>) item.getParent()).getSourceChildren().remove(item);
                }
            });
            executor.execute();
        });
    }

    private void getCurrentPartitions(Consumer<Integer> consumer) {
        selected((item, value) -> {
            value.loading(true);
            this.treeView.refresh();
            GetPartitionTaskExecutor executor = new GetPartitionTaskExecutor(value.clusterId(), value.label());
            executor.success(list -> {
                value.loading(false);
                this.treeView.refresh();
                consumer.accept(list.size());
            });
            executor.execute();
        });
    }

    private void addTopicAction() {
        this.addTopic.setOnAction(event -> {
            item().ifPresent(item -> ofNullable(item.getValue()).ifPresent(value -> {
                AddTopicPane pane = new AddTopicPane(value.clusterId());
                Stage stage = StageUtils.show(pane, SettingClient.bundle().getString("form.create.topic.title"), Modality.NONE);
                pane.setOnCancel(e -> stage.close());
                pane.setOnSave(success -> {
                    stage.close();
                    item.getSourceChildren().add(new FilterableTreeItem<>(success));
                    MessageUtils.success(SettingClient.bundle().getString("form.create.topic.success"));
                }, fail -> AlertUtils.error(stage, fail.getMessage()));
            }));
        });
    }

    /**
     * 执行删除Topic
     *
     * @param item  项目
     * @param value 价值
     */
    private static void executeDeleteTopic(FilterableTreeItem<ClusterNode> item, ClusterNode value) {
        AlertUtils.confirm(SettingClient.bundle().getString("alert.delete.prompt")).ifPresent(type -> {
            DeleteTopicTask task = new DeleteTopicTask(value.clusterId(), value.label());
            task.setOnSucceeded(e -> {
                FilterableTreeItem<ClusterNode> parent = (FilterableTreeItem<ClusterNode>) item.getParent();
                if (parent != null) {
                    parent.getSourceChildren().remove(item);
                }
                MessageUtils.success(SettingClient.bundle().getString("form.delete.success"));
            });
            task.setOnFailed(e -> {
                AlertUtils.error(State.stage(), e.getSource().getException().getMessage());
            });
            new Thread(task).start();
        });
    }

    private void addRefreshConsumerAction() {
        this.refreshConsumer.setOnAction(event -> selected((item, value) -> refresh(item, new QueryConsumersTask(value.clusterId()))));
    }


    /**
     * 刷新-topic 和 consumer
     *
     * @param item 项目
     * @param task 任务
     */
    private void refresh(FilterableTreeItem<ClusterNode> item, QueryTask<List<ClusterNode>> task) {
        item.getValue().loading(true);
        this.treeView.refresh();
        task.setOnSucceeded(e -> {
            item.getValue().loading(false);
            try {
                List<ClusterNode> nodes = task.get();
                Platform.runLater(() -> {
                    item.getSourceChildren().clear();
                    for (ClusterNode node : nodes) {
                        item.getSourceChildren().add(new FilterableTreeItem<>(node));
                    }
                });
                MessageUtils.success(SettingClient.bundle().getString("alert.refresh.success"));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            this.treeView.refresh();
        });
        task.setOnFailed(event -> {
            item.getValue().loading(false);
            AlertUtils.confirm(event.getSource().getException().getMessage());
            this.treeView.refresh();
        });
        new Thread(task).start();
    }

    private void addRefreshTopicAction() {
        this.refreshTopic.setOnAction(event -> {
            item().ifPresent(item -> ofNullable(item.getValue()).ifPresent(value -> refresh(item, new QueryTopicsTask(value.clusterId()))));
        });
    }

    /**
     * 获取当前值
     *
     * @return {@link Optional}<{@link ClusterNode}>
     */
    private Optional<ClusterNode> value() {
        if (item().isPresent()) {
            return ofNullable(item().get().getValue());
        }
        return Optional.empty();
    }


    /**
     * 获取当前值
     *
     * @return {@link Optional}<{@link ClusterNode}>
     */
    private Optional<FilterableTreeItem<ClusterNode>> item() {
        MultipleSelectionModel<TreeItem<ClusterNode>> model = this.treeView.getSelectionModel();
        if (model != null) {
            FilterableTreeItem<ClusterNode> item = (FilterableTreeItem<ClusterNode>) model.getSelectedItem();
            return ofNullable(item);
        }
        return Optional.empty();
    }

    @Override
    public void changed(ObservableValue<? extends TreeItem<ClusterNode>> observable, TreeItem<ClusterNode> oldValue, TreeItem<ClusterNode> newValue) {
        ContextMenu contextMenu = treeView.getContextMenu();
        ObservableList<MenuItem> items = contextMenu.getItems();
        items.clear();
        value().ifPresent(node -> {
            switch (node.type()) {
                case cluster -> items.addAll(disconnect, splitter, copy);
                case nodes -> items.add(showCluster);
                case node -> items.add(copy);
                case topics -> items.addAll(addTopic, refreshTopic);
                case topic ->
                        items.addAll(showPartition, offsetTopic, addPartition, splitter, deleteTopic, splitter1, copy);
                case consumers -> items.addAll(refreshConsumer);
                case consumer -> items.addAll(showConsumer, splitter, deleteConsumer, splitter1, copy);
            }
        });
    }
}
