package com.podigua.kafka.visark.home.layout;

import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import com.podigua.kafka.admin.task.CreateTopicTask;
import com.podigua.kafka.core.utils.NodeUtils;
import com.podigua.kafka.visark.home.control.FilterableTreeItem;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.util.StringUtils;

import java.util.function.Consumer;

/**
 * 添加主题窗格
 *
 * @author podigua
 * @date 2024/03/23
 */
public class AddTopicPane extends BorderPane {
    private final static double WIDTH = 220;
    private final String clusterId;

    /**
     * 主题
     */
    private final SimpleStringProperty topic = new SimpleStringProperty("");
    /**
     * 分区
     */
    private final SimpleIntegerProperty partition = new SimpleIntegerProperty(1);
    /**
     * 复制品
     */
    private final SimpleIntegerProperty replica = new SimpleIntegerProperty(1);

    /**
     * 名称表单
     */
    private Tile nameTile;
    /**
     * 取消
     */
    private Button cancel;
    /**
     * 保存
     */
    private Button save;
    private final ProgressIndicator progress = NodeUtils.progress();

    private final FontIcon saveIcon = new FontIcon(Material2MZ.SAVE);

    public AddTopicPane(String clusterId) {
        this.clusterId = clusterId;
        addCenter();
        addBottom();
        this.setPrefSize(400, 208);
    }

    private void addCenter() {
        VBox box = new VBox();
        box.setSpacing(10);
        nameTile = topicForm();
        /**
         * 分区表单
         */
        Tile partitionTile = partitionForm();
        /**
         * 副本表单
         */
        Tile replicaTile = replicaForm();
        box.getChildren().addAll(nameTile, partitionTile, replicaTile);
        this.setCenter(box);
    }

    private Tile replicaForm() {
        Tile tile = new Tile("Replica", "");
        Spinner<Integer> field = new Spinner<>(1, Integer.MAX_VALUE, replica.get());
        field.setPrefWidth(WIDTH);
        tile.setAction(field);
        tile.setActionHandler(field::requestFocus);
        replica.bind(field.valueProperty());
        return tile;
    }

    private Tile partitionForm() {
        Tile tile = new Tile("Partition", "");
        Spinner<Integer> field = new Spinner<>(1, Integer.MAX_VALUE, partition.get());
        field.setPrefWidth(WIDTH);
        tile.setAction(field);
        tile.setActionHandler(field::requestFocus);
        partition.bind(field.valueProperty());
        return tile;
    }

    private Tile topicForm() {
        Tile tile = new Tile("Topic", "");
        TextField field = new TextField(topic.get());
        field.setPrefWidth(WIDTH);
        tile.setAction(field);
        field.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                nameTile.setDescription("");
            }
        });
        tile.setActionHandler(field::requestFocus);
        topic.bind(field.textProperty());
        return tile;
    }

    private void addBottom() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setSpacing(10);
        box.setSpacing(5);
        cancel = new Button(SettingClient.bundle().getString("form.cancel"));
        cancel.setGraphic(new FontIcon(Material2AL.CANCEL));
        cancel.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.TEXT);
        save = new Button(SettingClient.bundle().getString("form.save"));
        save.setGraphic(saveIcon);
        save.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        box.getChildren().addAll(cancel, save);
        this.setBottom(box);
    }

    /**
     * 在“取消”时设置
     *
     * @param handler 处理器
     */
    public void setOnCancel(EventHandler<ActionEvent> handler) {
        this.cancel.setOnAction(handler);
    }

    /**
     * 设置上保存
     *
     * @param successConsumer 成功消费者
     * @param failConsumer    失败消费者
     */
    public void setOnSave(Consumer<ClusterNode> successConsumer, Consumer<Throwable> failConsumer) {
        this.save.setOnAction((e) -> {
            if (!StringUtils.hasText(topic.get())) {
                nameTile.setDescription("[color=red]".concat(SettingClient.bundle().getString("form.create.topic.required")).concat("[/color]"));
                return;
            }
            save.setGraphic(progress);
            CreateTopicTask task = new CreateTopicTask(clusterId, topic(), partition(), replica());
            task.setOnSucceeded(event -> {
                try {
                    save.setGraphic(saveIcon);
                    successConsumer.accept(task.get());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            task.setOnFailed(event -> {
                save.setGraphic(saveIcon);
                failConsumer.accept(event.getSource().getException());
            });
            Thread.ofVirtual().start(task);
        });
    }


    /**
     * 项目
     *
     * @return {@link FilterableTreeItem}<{@link ClusterNode}>
     */

    /**
     * 主题
     *
     * @return {@link String}
     */
    public String topic() {
        return topic.get();
    }

    /**
     * 分区
     *
     * @return int
     */
    public int partition() {
        return partition.get();
    }

    /**
     * 复制品
     *
     * @return short
     */
    public short replica() {
        return (short) replica.get();
    }
}
