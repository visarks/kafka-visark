package com.podigua.kafka.visark.home.controller;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import com.podigua.kafka.State;
import com.podigua.kafka.Windows;
import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.admin.task.QueryConsumersTask;
import com.podigua.kafka.admin.task.QueryNodesTask;
import com.podigua.kafka.admin.task.QueryTopicsTask;
import com.podigua.kafka.core.event.NoticeCloseEvent;
import com.podigua.kafka.core.event.NoticeEvent;
import com.podigua.kafka.core.utils.AlertUtils;
import com.podigua.kafka.core.utils.NodeUtils;
import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.core.utils.StageUtils;
import com.podigua.kafka.event.EventBus;
import com.podigua.kafka.event.ExitPublishEvent;
import com.podigua.kafka.event.TooltipEvent;
import com.podigua.kafka.gift.controller.GiftController;
import com.podigua.kafka.updater.Updater;
import com.podigua.kafka.visark.cluster.controller.ClusterController;
import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import com.podigua.kafka.visark.cluster.event.ClusterCloseEvent;
import com.podigua.kafka.visark.cluster.event.ClusterConnectEvent;
import com.podigua.kafka.visark.home.control.ClusterNodeChangeListener;
import com.podigua.kafka.visark.home.control.ClusterNodeTreeCell;
import com.podigua.kafka.visark.home.control.FilterableTreeItem;
import com.podigua.kafka.visark.home.control.MainTab;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import com.podigua.kafka.visark.home.enums.NodeType;
import com.podigua.kafka.visark.home.event.ClusterPublishEvent;
import com.podigua.kafka.visark.home.layout.ContentBorderPane;
import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.kafka.visark.setting.ThemeChangeEvent;
import com.podigua.kafka.visark.setting.enums.Themes;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.ColorScheme;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsFilled;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;
import org.kordamp.ikonli.remixicon.RemixiconAL;
import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * 主页 控制器
 *
 * @author podigua
 * @date 2024/03/18
 */
public class HomeController implements Initializable {
    private final FontIcon moonFontIcon = new FontIcon(FluentUiRegularMZ.WEATHER_MOON_16);
    private final FontIcon sunFontIcon = new FontIcon(Material2OutlinedMZ.WB_SUNNY);
    public Button clusterButton;
    public Button settingButton;
    public CustomTextField filter;
    public TreeView<ClusterNode> treeView;
    public TabPane tabPane;
    public AnchorPane leftPane;
    public Button toggleLeftButton;
    public SplitPane splitPane;
    public SimpleBooleanProperty isLight = new SimpleBooleanProperty();

    private final Timeline timeline = new Timeline();
    public AnchorPane rootPane;
    /**
     * 进展
     */
    private final ProgressIndicator progress = NodeUtils.progress();
    /**
     * 右
     */
    private final FontIcon right = new FontIcon(Material2OutlinedAL.CHECK_CIRCLE);

    public HBox state;
    public HBox tooltipBox;
    public Button giteeButton;
    public Button themeButton;
    public BorderPane contentPane;
    public Button giftButton;
    private FilterableTreeItem<ClusterNode> root;

    private final Timeline tooltipTimer = new Timeline(new KeyFrame(Duration.seconds(3)));

    public HomeController() {
        EventBus.getInstance().subscribe(ClusterConnectEvent.class, event -> {
            ClusterProperty property = event.property();
            ClusterNode root = ClusterNode.cluster(property.getId(), property.getName(), property.getId());
            FilterableTreeItem<ClusterNode> cluster = new FilterableTreeItem<>(root);
            cluster.setExpanded(true);
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
            addTab(root);
            double[] positions = splitPane.getDividerPositions();
            if (positions.length == 1 && positions[0] < 0.1) {
                this.toggleLeft(null);
            }
        });
        EventBus.getInstance().subscribe(ClusterCloseEvent.class, event -> {
            String clusterId = event.clusterId();
            Iterator<TreeItem<ClusterNode>> iterator = this.root.getSourceChildren().iterator();
            while (iterator.hasNext()) {
                TreeItem<ClusterNode> item = iterator.next();
                if (item.getValue().clusterId().equals(clusterId)) {
                    iterator.remove();
                    break;
                }
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
//        EventBus.getInstance().subscribe(LoadingEvent.class, event -> {
//            state.getChildren().clear();
//            if (event.loading()) {
//                state.getChildren().add(progress);
//            } else {
//                state.getChildren().add(right);
//            }
//        });
        EventBus.getInstance().subscribe(ClusterPublishEvent.class, event -> {
            addTab(event.node());
        });
        EventBus.getInstance().subscribe(ExitPublishEvent.class, event -> {
            close();
        });
        EventBus.getInstance().subscribe(TooltipEvent.class, event -> {
            Platform.runLater(() -> {
                tooltipTimer.stop();
                tooltipTimer.play();
                tooltipBox.getChildren().clear();
                Label label = new Label(event.tooltip());
                tooltipBox.getChildren().add(label);
            });
        });
    }

    private void close() {
        ObservableList<Tab> tabs = this.tabPane.getTabs();
        for (Tab tab : tabs) {
            ContentBorderPane pane = (ContentBorderPane) tab.getUserData();
            pane.close();
        }
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
        Thread.ofVirtual().start(task);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTheme();
        tooltipTimer.setOnFinished(event -> {
            tooltipBox.getChildren().clear();
        });
        right.getStyleClass().add(Styles.SUCCESS);
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
        giteeButton.setGraphic(new FontIcon(AntDesignIconsOutlined.GITHUB));
        giftButton.setGraphic(new FontIcon(AntDesignIconsFilled.GIFT));
        toggleLeftButton.setGraphic(new FontIcon(RemixiconAL.LAYOUT_LEFT_LINE));
        clusterButton.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
        settingButton.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
        giteeButton.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
        giftButton.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
        toggleLeftButton.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
        resetTheme();
        filter.setLeft(new FontIcon(Material2OutlinedAL.FILTER_ALT));
        filter.setRight(clear);
        root = new FilterableTreeItem<>(ClusterNode.root());
        treeView.setRoot(root);
//        FilteredList<TreeItem<ClusterNode>> filters = new FilteredList<>(FXCollections.observableArrayList(root.getChildren()));
//        filter.textProperty().addListener((observable, oldValue, newValue) -> {
//            filters.setPredicate(item -> {
//                if (item == null || item.getValue() == null) {
//                    return false;
//                }
//                return item.getValue().label().toLowerCase().contains(newValue.toLowerCase());
//            });
//        });
        treeView.setShowRoot(false);
        treeView.setCellFactory(param -> new ClusterNodeTreeCell());
        treeView.setContextMenu(new ContextMenu());
        treeView.getSelectionModel().selectedItemProperty().addListener(new ClusterNodeChangeListener(treeView, this.tabPane));
        openDialog();
        openCheckUpdate();
        treeView.setOnMouseClicked(event -> {
            if (MouseButton.PRIMARY.equals(event.getButton()) && event.getClickCount() == 2) {
                Optional.ofNullable(treeView.getSelectionModel()).flatMap(model -> Optional.ofNullable(model.getSelectedItem())).ifPresent(item -> {
                    addTab(item.getValue());
                });
            }
        });
    }

    private void openCheckUpdate() {
        if (Boolean.TRUE.equals(SettingClient.get().getAutoUpdater())) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3)));
            timeline.setOnFinished(e -> Updater.check(false));
            timeline.play();
        }
    }

    private void initTheme() {
        EventBus.getInstance().subscribe(ThemeChangeEvent.class, event -> {
            resetThemeIcon();
        });
        themeButton.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
        isLight.addListener((observable, oldValue, newValue) -> {
            changeThemeIcon(newValue);
        });
        resetThemeIcon();
    }

    private void changeThemeIcon(Boolean newValue) {
        if (newValue) {
            themeButton.setGraphic(moonFontIcon);
        } else {
            themeButton.setGraphic(sunFontIcon);
        }
    }

    private void resetThemeIcon() {
        if (SettingClient.get().getAutoTheme()) {
            ColorScheme scheme = Platform.getPreferences().getColorScheme();
            if (ColorScheme.DARK.equals(scheme)) {
                isLight.setValue(false);
            } else {
                isLight.setValue(true);
            }
        } else {
            Themes theme = SettingClient.get().getTheme();
            switch (theme) {
                case nord_dark:
                case primer_dark:
                case cupertino_dark:
                case dracula:
                    isLight.setValue(false);
                    break;
                case nord_light:
                case primer_light:
                case cupertino_light:
                    isLight.setValue(true);
                    break;
            }
        }
        changeThemeIcon(isLight.getValue());
    }

    private void resetTheme() {

    }


    private void addTab(ClusterNode value) {
        ObservableList<Tab> tabs = this.tabPane.getTabs();
        for (Tab tab : tabs) {
            if (Objects.equals(value.id(), tab.getId())) {
                this.tabPane.getSelectionModel().select(tab);
                return;
            }
        }
        boolean contains = tabs.stream().map(Tab::getId).toList().contains(value.id());
        if (contains) {
            return;
        }
        if (!NodeType.topic.equals(value.type()) && !NodeType.consumer.equals(value.type())) {
            return;
        }
        this.tabPane.getTabs().add(new MainTab(value));
        this.tabPane.getSelectionModel().select(this.tabPane.getTabs().size() - 1);

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
        Windows.setting();
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

    public void showGit(ActionEvent event) {
        State.hostServices().showDocument(State.GITHUB);
    }

    public void changeTheme(ActionEvent event) {
        isLight.setValue(!isLight.getValue());
        play();
        if (isLight.getValue()) {
            Application.setUserAgentStylesheet(Themes.primer_light.theme().getUserAgentStylesheet());
        } else {
            Application.setUserAgentStylesheet(Themes.primer_dark.theme().getUserAgentStylesheet());
        }

    }

    public static void play() {
        if (Platform.isSupported(ConditionalFeature.SHAPE_CLIP)) {
//            Point point = MouseInfo.getPointerInfo().getLocation();
//            Scene scene = State.stage().getScene();
//            Circle circle1 = new Circle(point.getX(), point.getY(), 20);
//            State.pane().setClip(circle1);
//            Timeline tl = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(circle1.radiusProperty(), Math.max(scene.getWidth(), scene.getHeight()) * 1.2)));
//            tl.setOnFinished(event1 -> {
//                State.pane().setClip(null);
//            });
//            tl.play();
        }
    }

    public void showGift(ActionEvent event) {
        FXMLLoader loader = Resources.getLoader("/fxml/gift.fxml");
        GiftController controller = loader.getController();
        Parent parent = loader.getRoot();
        Stage stage = StageUtils.none(parent);
        stage.show();
    }

    public void clear(ActionEvent event) {
        ManagementFactory.getMemoryMXBean().gc();
    }
}
