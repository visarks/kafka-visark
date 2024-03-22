package com.podigua.kafka.visark.home.controller;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import com.podigua.kafka.State;
import com.podigua.kafka.admin.*;
import com.podigua.kafka.admin.task.QueryConsumersTask;
import com.podigua.kafka.admin.task.QueryNodesTask;
import com.podigua.kafka.admin.task.QueryTopicsTask;
import com.podigua.kafka.core.event.NoticeCloseEvent;
import com.podigua.kafka.core.event.NoticeEvent;
import com.podigua.kafka.core.utils.AlertUtils;
import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.core.utils.StageUtils;
import com.podigua.kafka.event.EventBus;
import com.podigua.kafka.visark.cluster.controller.ClusterController;
import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import com.podigua.kafka.visark.cluster.event.ClusterConnectEvent;
import com.podigua.kafka.visark.home.control.ClusterNodeChangeListener;
import com.podigua.kafka.visark.home.control.ClusterNodeTreeCell;
import com.podigua.kafka.visark.home.control.FilterableTreeItem;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;
import org.kordamp.ikonli.remixicon.RemixiconAL;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * 主页 控制器
 *
 * @author podigua
 * @date 2024/03/18
 */
public class HomeController implements Initializable {
    public Button clusterButton;
    public Button settingButton;
    public CustomTextField filter;
    public TreeView<ClusterNode> treeView;
    public TabPane tabPane;
    public AnchorPane leftPane;
    public Button toggleLeftButton;
    public SplitPane splitPane;

    private final Timeline timeline = new Timeline();
    public AnchorPane rootPane;
    private FilterableTreeItem<ClusterNode> root;

    public HomeController() {
        EventBus.getInstance().subscribe(ClusterConnectEvent.class, event -> {
            ClusterProperty property = event.property();
            FilterableTreeItem<ClusterNode> cluster = new FilterableTreeItem<>(ClusterNode.cluster(property.getId(),property.getName(), property.getId()));
            this.root.getSourceChildren().add(cluster);
            FilterableTreeItem<ClusterNode> nodes = new FilterableTreeItem<>(ClusterNode.nodes(property.getId()));
            cluster.getSourceChildren().add(nodes);
            FilterableTreeItem<ClusterNode> topics = new FilterableTreeItem<>(ClusterNode.topics(property.getId()));
            cluster.getSourceChildren().add(topics);
            FilterableTreeItem<ClusterNode> consumers = new FilterableTreeItem<>(ClusterNode.consumers(property.getId()));
            cluster.getSourceChildren().add(consumers);
            query(nodes, new QueryNodesTask(property.getId()));
            query(topics, new QueryTopicsTask(property.getId()));
            query(consumers, new QueryConsumersTask(property.getId()));
            double[] positions = splitPane.getDividerPositions();
            if (positions.length == 1 && positions[0] < 0.1) {
                this.toggleLeft(null);
            }
        });
        onMessage();
    }

    private void onMessage() {
        EventBus.getInstance().subscribe(NoticeEvent.class, event -> {
            Notification notification = event.notification();
            rootPane.getChildren().add(notification);
            Duration duration = event.duration();
            if (Duration.ZERO != duration) {
                var timeline = new Timeline(new KeyFrame(duration));
                timeline.setOnFinished(event1 -> notification.getOnClose().handle(new Event(Event.ANY)));
                timeline.play();
            }
        });
        EventBus.getInstance().subscribe(NoticeCloseEvent.class, event -> {
            Notification notification = event.notification();
            rootPane.getChildren().remove(notification);
        });
    }

    private void query(FilterableTreeItem<ClusterNode> parent, QueryTask<List<ClusterNode>> task) {
        parent.getValue().loading(true);
        this.treeView.refresh();
        task.setOnSucceeded(event -> {
            try {
                List<ClusterNode> values = task.get();
                values.forEach(value -> parent.getSourceChildren().add(new FilterableTreeItem<>(value)));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            parent.getValue().loading(false);
            this.treeView.refresh();
        });
        task.setOnFailed(event -> {
            parent.getValue().loading(false);
            this.treeView.refresh();
            AlertUtils.error(State.stage(), AdminManger.translate(event.getSource().getException()).getMessage());
        });
        new Thread(task).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            root.predicateProperty().set(node -> {
                if (node == null || !StringUtils.hasText(newValue)) {
                    return true;
                }
                return node.label().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        FontIcon clear = new FontIcon(Material2OutlinedAL.CLOSE);
        clear.getStyleClass().add(Styles.DANGER);
        clear.setCursor(Cursor.DEFAULT);
        clear.setOnMouseClicked(event -> {
            filter.setText("");
        });
        clusterButton.setGraphic(new FontIcon(Material2OutlinedAL.FOLDER));
        settingButton.setGraphic(new FontIcon(Material2OutlinedMZ.SETTINGS));
        toggleLeftButton.setGraphic(new FontIcon(RemixiconAL.LAYOUT_LEFT_LINE));
        filter.setLeft(new FontIcon(Material2OutlinedAL.FILTER_ALT));
        filter.setRight(clear);
        root = new FilterableTreeItem<>(ClusterNode.root());
        treeView.setRoot(root);
        FilteredList<TreeItem<ClusterNode>> filters = new FilteredList<>(FXCollections.observableArrayList(root.getChildren()));
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            filters.setPredicate(item -> {
                if (item == null || item.getValue() == null) {
                    return false;
                }
                return item.getValue().label().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        treeView.setShowRoot(false);
        treeView.setCellFactory(param -> new ClusterNodeTreeCell());
        treeView.setContextMenu(new ContextMenu());
        treeView.getSelectionModel().selectedItemProperty().addListener(new ClusterNodeChangeListener(treeView));
        openDialog();
    }

    private void openDialog() {
        if (SettingClient.get().getOpenDialog()) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1)));
            timeline.setOnFinished(e -> {
                showCluster(null);
            });
            timeline.play();
        }
    }

    public void showSetting(ActionEvent event) {
        FXMLLoader loader = Resources.getLoader("/fxml/setting.fxml");
        Parent parent = loader.getRoot();
        StageUtils.show(parent, SettingClient.bundle().getString("setting.title"));

    }

    public void showCluster(ActionEvent event) {
        FXMLLoader loader = Resources.getLoader("/fxml/cluster.fxml");
        Parent parent = loader.getRoot();
        Stage stage = StageUtils.show(parent, SettingClient.bundle().getString("cluster.title"), State.stage());
        ClusterController controller = loader.getController();
        controller.setParentStage(stage);
    }

    public void toggleLeft(ActionEvent event) {
        double[] positions = splitPane.getDividerPositions();
        if (positions.length == 1) {
            timeline.stop();
            timeline.getKeyFrames().clear();
            if (positions[0] <= 0.1) {
                timeline.getKeyFrames().addAll(
                        new KeyFrame(Duration.ZERO, new KeyValue(splitPane.getDividers().get(0).positionProperty(), positions[0])),
                        new KeyFrame(Duration.millis(300), new KeyValue(splitPane.getDividers().get(0).positionProperty(), 240 / splitPane.getWidth()))
                );
            } else {
                timeline.getKeyFrames().addAll(
                        new KeyFrame(Duration.ZERO, new KeyValue(splitPane.getDividers().get(0).positionProperty(), positions[0])),
                        new KeyFrame(Duration.millis(300), new KeyValue(splitPane.getDividers().get(0).positionProperty(), 0))
                );
            }
            timeline.play();
        }
        SplitPane.setResizableWithParent(leftPane, false);
    }
}
