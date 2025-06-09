package com.podigua.kafka.visark.home.layout;

import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import com.podigua.kafka.admin.task.AddPartitionTask;
import com.podigua.kafka.core.utils.NodeUtils;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

import java.util.function.Consumer;

/**
 * 添加分区窗格
 *
 * @author podigua
 * @date 2024/03/23
 */
public class AddPartitionPane extends BorderPane {
    private final static double WIDTH = 180;
    private final String clusterId;
    /**
     * 主题
     */
    private final String topic;
    /**
     * 当前值
     */
    private final Integer current;
    /**
     * 复制品
     */
    private final SimpleIntegerProperty newValue = new SimpleIntegerProperty();
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

    public AddPartitionPane(String clusterId, String topic, Integer current) {
        this.clusterId = clusterId;
        this.topic = topic;
        this.current = current;
        this.newValue.set(current+1);
        addCenter();
        addBottom();
        this.setPrefSize(360, 170);
    }

    private void addCenter() {
        VBox box = new VBox();
        box.setSpacing(10);
        Tile newValueTile = newValueTileForm();
        Tile currentTile = currentValueForm();
        box.getChildren().addAll(newValueTile, currentTile);
        this.setCenter(box);
    }

    private Tile currentValueForm() {
        Tile tile = new Tile(SettingClient.bundle().getString("form.add.partition.current"), "");
        Spinner<Integer> field = new Spinner<>(current, Integer.MAX_VALUE, current);
        field.setPrefWidth(WIDTH);
        tile.setAction(field);
        field.setDisable(true);
        return tile;
    }

    private Tile newValueTileForm() {
        Tile tile = new Tile(SettingClient.bundle().getString("form.add.partition.new.value"), "");
        Spinner<Integer> field = new Spinner<>(current + 1, Integer.MAX_VALUE, newValue.get());
        field.setPrefWidth(WIDTH);
        tile.setAction(field);
        tile.setActionHandler(field::requestFocus);
        newValue.bind(field.valueProperty());
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
    public void setOnSave(Consumer<Void> successConsumer, Consumer<Throwable> failConsumer) {
        this.save.setOnAction((e) -> {
            save.setGraphic(progress);
            AddPartitionTask task = new AddPartitionTask(clusterId, topic,newValue.get());
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
}
