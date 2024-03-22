package com.podigua.kafka.visark.home.control;

import atlantafx.base.theme.Styles;
import com.podigua.kafka.State;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.admin.task.DeleteTopicTask;
import com.podigua.kafka.admin.task.QueryConsumersTask;
import com.podigua.kafka.admin.task.QueryTopicsTask;
import com.podigua.kafka.core.utils.AlertUtils;
import com.podigua.kafka.core.utils.ClipboardUtils;
import com.podigua.kafka.core.utils.MessageUtils;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.codicons.Codicons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * 群集节点更改侦听器
 *
 * @author podigua
 * @date 2024/03/22
 */
public class ClusterNodeChangeListener implements ChangeListener<TreeItem<ClusterNode>> {
    private final TreeView<ClusterNode> treeView;

    /**
     * 断开
     */
    private final MenuItem disconnect;
    /**
     * 消费者详细信息
     */
    private final MenuItem consumerDetail;
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
    private final MenuItem deleteTopic;
    /**
     * 偏移主题
     */
    private final MenuItem offsetTopic;
    /**
     * 偏移消费品
     */
    private final MenuItem offsetConsumer;
    /**
     * 会员消费者
     */
    private final MenuItem memberConsumer;
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


    public ClusterNodeChangeListener(TreeView<ClusterNode> treeView) {
        this.treeView = treeView;
        FontIcon close = new FontIcon(Codicons.CLOSE);
        close.getStyleClass().add(Styles.DANGER);
        this.disconnect = new MenuItem(SettingClient.bundle().getString("context.menu.disconnect"), close);
        this.consumerDetail = new MenuItem(SettingClient.bundle().getString("context.menu.show.details"), new FontIcon(Material2AL.ARTICLE));
        this.addTopic = new MenuItem(SettingClient.bundle().getString("context.menu.create"), new FontIcon(Material2AL.ADD));
        this.refreshTopic = new MenuItem(SettingClient.bundle().getString("context.menu.refresh"), new FontIcon(Material2MZ.REFRESH));
        this.offsetTopic = new MenuItem(SettingClient.bundle().getString("context.menu.offset"), new FontIcon(Material2MZ.OUTLINED_FLAG));
        this.offsetConsumer = new MenuItem(SettingClient.bundle().getString("context.menu.refresh"), new FontIcon(Material2MZ.REFRESH));
        this.memberConsumer = new MenuItem(SettingClient.bundle().getString("context.menu.members"), new FontIcon(Material2AL.GROUP));
        this.addPartition = new MenuItem(SettingClient.bundle().getString("context.menu.add.partition"), new FontIcon(Material2AL.ADD));
        FontIcon delete = new FontIcon(Material2AL.DELETE);
        delete.getStyleClass().add(Styles.DANGER);
        this.deleteTopic = new MenuItem(SettingClient.bundle().getString("context.menu.delete"), delete);
        this.refreshConsumer = new MenuItem(SettingClient.bundle().getString("context.menu.refresh"), new FontIcon(Material2MZ.REFRESH));
        this.copy = new MenuItem(SettingClient.bundle().getString("context.menu.copy"), new FontIcon(AntDesignIconsOutlined.COPY));
        addAction();
    }

    private void addAction() {
        this.copy.setOnAction(event -> value().ifPresent(node -> ClipboardUtils.copy(node.label())));
        addRefreshTopicAction();
        addDeleteTopicAction();
        addRefreshConsumerAction();
    }

    private void addDeleteTopicAction() {
        this.deleteTopic.setOnAction(event -> {
            item().ifPresent(item -> {
                ClusterNode value = item.getValue();
                if (value != null) {
                    AlertUtils.confirm(SettingClient.bundle().getString("alert.delete.prompt")).ifPresent(type -> {
                        DeleteTopicTask task = new DeleteTopicTask(value.clusterId(), value.label());
                        task.setOnSucceeded(e -> {
                            FilterableTreeItem<ClusterNode> parent = (FilterableTreeItem<ClusterNode>) item.getParent();
                            if (parent != null) {
                                parent.getSourceChildren().remove(item);
                            }
                            MessageUtils.show(SettingClient.bundle().getString("form.delete.success"));
                        });
                        task.setOnFailed(e -> {
                            AlertUtils.error(State.stage(), e.getSource().getException().getMessage());
                        });
                        new Thread(task).start();
                    });
                }
            });
        });
    }

    private void addRefreshConsumerAction() {
        this.refreshConsumer.setOnAction(event -> {
            item().ifPresent(item -> ofNullable(item.getValue()).ifPresent(value -> {
                refresh(item, new QueryConsumersTask(value.clusterId()));
            }));
        });
    }


    private void refresh(FilterableTreeItem<ClusterNode> item, QueryTask<List<ClusterNode>> task) {
        task.setOnSucceeded(e -> {
            try {
                List<ClusterNode> nodes = task.get();
                Platform.runLater(() -> {
                    item.getSourceChildren().clear();
                    for (ClusterNode node : nodes) {
                        item.getSourceChildren().add(new FilterableTreeItem<>(node));
                    }
                });
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
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
                case nodes -> items.add(consumerDetail);
                case node -> items.add(copy);
                case topics -> items.addAll(addTopic, refreshTopic);
                case topic -> items.addAll(offsetTopic, addPartition, splitter, deleteTopic, splitter1, copy);
                case consumers -> items.addAll(refreshConsumer);
                case consumer -> items.addAll(offsetConsumer, memberConsumer, splitter, copy);
            }
        });
    }
}
