package com.podigua.kafka.visark.cluster.controller;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.podigua.kafka.admin.AdminConnectTask;
import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.core.FilterValue;
import com.podigua.kafka.core.utils.AlertUtils;
import com.podigua.kafka.core.utils.MessageUtils;
import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.core.utils.StageUtils;
import com.podigua.kafka.visark.cluster.ClusterClient;
import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import com.podigua.kafka.visark.cluster.event.ClusterConnectEvent;
import com.podigua.kafka.visark.cluster.layout.ConnectPane;
import com.podigua.kafka.visark.home.control.FilterableTreeItem;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * 集群控制器
 *
 * @author podigua
 * @date 2024/03/21
 */
public class ClusterController implements Initializable {
    private final static Logger logger = LoggerFactory.getLogger(ClusterController.class);
    public Button addButton;
    public Button deleteButton;
    public Button connectButton;
    public TreeTableView<ClusterProperty> tableView;
    public Button editButton;
    public CheckBox openDialog;
    public TextField filterField;
    public Button addFolderButton;
    private Stage parentStage;

    public CustomTextField filter;
    private FilterableTreeItem<ClusterProperty> root;


    public static final <T> void bindTableViewFilter(TableView<T> tableView, ObservableList<T> observableList, FilterValue<T> filterValue, String newValue) {
        FilteredList<T> filteredData = new FilteredList<>(observableList, p -> true);
        filteredData.setPredicate(entity -> filterValue.compare(entity, newValue));
        SortedList<T> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }

    private Predicate<ClusterProperty> predicate(String newValue) {
        return new Predicate<ClusterProperty>() {
            @Override
            public boolean test(ClusterProperty node) {
                if (node == null || !StringUtils.hasText(newValue)) {
                    return true;
                }
                boolean nameMatch = false;
                if (StringUtils.hasText(node.getName()) && node.getName().toLowerCase().contains(newValue.toLowerCase())) {
                    nameMatch = true;
                }
                boolean serverMatch = false;
                if (StringUtils.hasText(node.getServers()) && node.getServers().toLowerCase().contains(newValue.toLowerCase())) {
                    serverMatch = true;
                }
                return nameMatch || serverMatch;
            }
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initFilter();
        initStyle();
        initTable();
        this.openDialog.setSelected(SettingClient.get().getOpenDialog());
        SettingClient.get().openDialog().bind(this.openDialog.selectedProperty());
    }

    private void initFilter() {
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            root.predicateProperty().set(predicate(newValue));
        });
//        FilteredList<TreeItem<ClusterProperty>> filters = new FilteredList<>(FXCollections.observableArrayList(root.getChildren()));
//        filter.textProperty().addListener((observable, oldValue, newValue) -> {
//            filters.setPredicate(item -> {
//                if (item == null || item.getValue() == null) {
//                    return false;
//                }
//                if (!StringUtils.hasText(newValue)) {
//                    return true;
//                }
//                boolean nameMatch = false;
//                if (StringUtils.hasText(item.getValue().getName()) && item.getValue().getName().toLowerCase().contains(newValue.toLowerCase())) {
//                    nameMatch = true;
//                }
//                boolean serverMatch = false;
//                if (StringUtils.hasText(item.getValue().getServers()) && item.getValue().getServers().toLowerCase().contains(newValue.toLowerCase())) {
//                    serverMatch = true;
//                }
//                return nameMatch || serverMatch;
//            });
//        });
        FontIcon clear = new FontIcon(Material2OutlinedAL.CLOSE);
        clear.getStyleClass().add(Styles.DANGER);
        clear.setCursor(Cursor.DEFAULT);
        clear.setOnMouseClicked(event -> {
            filter.setText("");
        });
        filter.setLeft(new FontIcon(Material2OutlinedAL.FILTER_ALT));
        filter.setRight(clear);
    }

    private void initTable() {
        this.root = new FilterableTreeItem<>(new ClusterProperty());
        this.tableView.setRoot(root);
        this.tableView.setShowRoot(false);
        tableView.getStyleClass().addAll(Styles.DENSE, Styles.BORDER_SUBTLE, Styles.STRIPED, Tweaks.EDGE_TO_EDGE);
        tableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
//        TreeTableColumn<ClusterProperty, String> priority = new TreeTableColumn<>("#");
//        priority.setCellFactory(col -> {
//            var cell = new TableCell<ClusterProperty, String>();
//            StringBinding value = Bindings.when(cell.emptyProperty()).then("").otherwise(cell.indexProperty().add(1).asString());
//            cell.textProperty().bind(value);
//            return cell;
//        });
        TreeTableColumn<ClusterProperty, String> name = new TreeTableColumn<>(SettingClient.bundle().getString("cluster.table.name"));
//        name.setCellFactory(clusterPropertyClusterPropertyTreeTableColumn -> new NameTreeTableCell());
        name.setCellValueFactory(property -> property.getValue().getValue().name());
        TreeTableColumn<ClusterProperty, String> servers = new TreeTableColumn<>(SettingClient.bundle().getString("cluster.table.servers"));
        servers.setCellValueFactory(property -> property.getValue().getValue().servers());
        tableView.getColumns().addAll(name, servers);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editButton.setDisable(false);
                deleteButton.setDisable(false);
                if (newValue.getValue() != null && "cluster".equals(newValue.getValue().getType())) {
                    connectButton.setDisable(false);
                } else {
                    connectButton.setDisable(true);
                }
            } else {
                editButton.setDisable(true);
                connectButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
        tableView.setOnMouseClicked(event -> {
            if (MouseButton.PRIMARY.equals(event.getButton()) && event.getClickCount() == 2) {
                getSelectValue().ifPresent(property -> {
                    if (!property._folder()) {
                        this.onConnect(null);
                    }
                });
            }
        });
        reload();
    }

    public void reload() {
        List<ClusterProperty> clusters = ClusterClient.query4List();
        this.root.getSourceChildren().clear();
        ObservableList<FilterableTreeItem<ClusterProperty>> list = buildTree(clusters, null);
        this.root.getSourceChildren().addAll(list);
        tableView.refresh();
    }

    private ObservableList<FilterableTreeItem<ClusterProperty>> buildTree(List<ClusterProperty> clusters, String parent) {
        ObservableList<FilterableTreeItem<ClusterProperty>> result = FXCollections.observableArrayList();
        for (ClusterProperty cluster : clusters) {
            boolean add = false;
            if (StringUtils.hasText(parent) && parent.equals(cluster.getParentId())) {
                add = true;
            }
            if (!StringUtils.hasText(parent) && !StringUtils.hasText(cluster.getParentId())) {
                add = true;
            }
            if (add) {
                FilterableTreeItem<ClusterProperty> item = new FilterableTreeItem<>(cluster);
                item.setExpanded(true);
                ObservableList<FilterableTreeItem<ClusterProperty>> children = buildTree(clusters, cluster.getId());
                item.getSourceChildren().addAll(children);
                result.add(item);
            }
        }
        return result;
    }

    private void initStyle() {
        addButton.setGraphic(new FontIcon(Material2AL.ADD));
        addButton.getStyleClass().addAll(Styles.FLAT, Styles.ACCENT);
        addFolderButton.setGraphic(new FontIcon(Material2AL.CREATE_NEW_FOLDER));
        addFolderButton.getStyleClass().addAll(Styles.FLAT, Styles.ACCENT);
        editButton.setGraphic(new FontIcon(Material2AL.EDIT));
        editButton.getStyleClass().addAll(Styles.FLAT, Styles.ACCENT);
        deleteButton.setGraphic(new FontIcon(Material2AL.DELETE));
        deleteButton.getStyleClass().addAll(Styles.FLAT, Styles.DANGER);
        connectButton.setGraphic(new FontIcon(Material2AL.LINK));
        connectButton.getStyleClass().addAll(Styles.FLAT, Styles.ACCENT);
    }

    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }

    public void onAdd(ActionEvent event) {
        Optional<ClusterProperty> parent = getSelectValue();
        String parentId = "";
        if (parent.isPresent()) {
            parentId = parent.get().getId();
        }
        ClusterProperty property = new ClusterProperty();
        property.setParentId(parentId);
        property.cluster();
        openForm(property, true);
    }

    private void openForm(ClusterProperty property, boolean isAdd) {
        FXMLLoader loader = Resources.getLoader("/fxml/cluster-form.fxml");
        ClusterFormController controller = loader.getController();
        String title = isAdd ? SettingClient.bundle().getString("form.new") : SettingClient.bundle().getString("form.edit");
        Stage formStage = StageUtils.show(loader.getRoot(), title, parentStage);
        controller.set(this, formStage, property, isAdd);
    }

    public void onEdit(ActionEvent event) {
        getSelectValue().ifPresent(property -> {
            if (property._folder()) {
                openFolderForm(property, false);
            } else {
                openForm(property, false);
            }
        });
    }

    public void onDelete(ActionEvent event) {
        TreeItem<ClusterProperty> item = tableView.getSelectionModel().getSelectedItem();
        AlertUtils.confirm(SettingClient.bundle().getString("alert.delete.prompt")).ifPresent(type -> {
            getSelectValue().ifPresent(property -> {
                ClusterClient.deleteById(property.getId());
                Optional.ofNullable(item.getParent()).ifPresent(parent -> {
                    parent.getChildren().remove(item);
                });
                MessageUtils.success(SettingClient.bundle().getString("form.delete.success"));
//                reload();
            });
        });
    }

    private Optional<ClusterProperty> getSelectValue() {
        TreeItem<ClusterProperty> item = tableView.getSelectionModel().getSelectedItem();
        if (item != null) {
            return Optional.ofNullable(item.getValue());
        }
        return Optional.empty();
    }

    public void onConnect(ActionEvent event) {
        getSelectValue().ifPresent(property -> {
            if (AdminManger.get(property.getId()) != null) {
                AlertUtils.error(parentStage, SettingClient.bundle().getString("alert.connect.tips"));
                return;
            }
            AdminConnectTask task = new AdminConnectTask(property);
            ConnectPane pane = new ConnectPane(e -> task.cancel());
            Stage stage = StageUtils.body(pane, parentStage);
            task.setOnSucceeded(e -> {
                logger.info("连接成功:{}" , property.getServers());
                try {
                    AdminManger.put(property.getId(), task.get());
                    MessageUtils.success(SettingClient.bundle().getString("alert.connect.success"));
                    new ClusterConnectEvent(property).publish();
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        throw new RuntimeException(ex);
                    });
                }
                stage.close();
                parentStage.close();
            });
            task.setOnFailed(e -> {
                logger.warn("连接失败:{}" , property.getServers());
                stage.close();
                Throwable translate = AdminManger.translate(e.getSource().getException());
                AlertUtils.error(parentStage, translate.getMessage());
            });
            task.setOnCancelled(e -> {
                logger.warn("取消连接:{}", property.getServers());
                stage.close();
            });
            new Thread(task).start();
        });

    }

    private void openFolderForm(ClusterProperty property, boolean isAdd) {
        FXMLLoader loader = Resources.getLoader("/fxml/folder-form.fxml");
        FolderFormController controller = loader.getController();
        String title = isAdd ? SettingClient.bundle().getString("form.new") : SettingClient.bundle().getString("form.edit");
        Stage formStage = StageUtils.show(loader.getRoot(), title, parentStage);
        controller.set(this, formStage, property, isAdd);
    }

    public void onAddFolder(ActionEvent actionEvent) {
        Optional<ClusterProperty> parent = getSelectValue();
        String parentId = "";
        if (parent.isPresent() && parent.get()._folder()) {
            parentId = parent.get().getId();
        }
        ClusterProperty property = new ClusterProperty();
        property.setParentId(parentId);
        property.folder();
        openFolderForm(property, true);
    }

    public void success(ClusterProperty clusterProperty, boolean isAdd) {
        if (isAdd) {
            TreeItem<ClusterProperty> item = new TreeItem<>(clusterProperty);
            item.setExpanded(true);
            this.root.getSourceChildren().add(item);
        } else {
            TreeItem<ClusterProperty> item = tableView.getSelectionModel().getSelectedItem();
            if (item != null) {
                item.setValue(clusterProperty);
            }
        }
    }
}
