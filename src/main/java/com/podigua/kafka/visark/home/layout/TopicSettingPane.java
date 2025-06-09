package com.podigua.kafka.visark.home.layout;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Tile;
import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import com.podigua.kafka.State;
import com.podigua.kafka.admin.task.TopicSettingTask;
import com.podigua.kafka.core.utils.NodeUtils;
import com.podigua.kafka.visark.home.convert.MessageConvertFactory;
import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.kafka.visark.settings.TopicSettingClient;
import com.podigua.kafka.visark.settings.entity.TopicSettingProperty;
import com.podigua.path.Paths;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsFilled;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.function.Consumer;

/**
 * 添加分区窗格
 *
 * @author podigua
 * @date 2024/03/23
 */
public class TopicSettingPane extends BorderPane {
    private final static double WIDTH = 400;
    private final String clusterId;
    /**
     * 主题
     */
    private final String topic;

    private TopicSettingProperty property;

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

    public TopicSettingPane(String clusterId, String topic) {
        this.clusterId = clusterId;
        this.topic = topic;
        property = TopicSettingClient.getByClusterAndTopic(this.clusterId, this.topic);
        addCenter();
        addBottom();
        this.setPrefSize(650, 450);
    }

    private void addCenter() {
        VBox root = new VBox();
        root.setSpacing(10);
        TitledPane keyPane = getKeyPane();
        TitledPane valuePane = getValuePane();
        root.getChildren().addAll(keyPane, valuePane);
        this.setCenter(root);
    }

    private TitledPane getValuePane() {
        VBox box = new VBox();
        box.setSpacing(10);
        Tile valueType = typeForm(property.valueTypeProperty());
        Tile charset = charsetForm(property.valueTypeProperty(), property.valueCharsetProperty());
        Tile protobuf = protobufForm(property.valueTypeProperty(), property.valueProtobufFileProperty());
        property.valueTypeProperty().addListener((observable, oldValue, newValue) -> {
            if (MessageConvertFactory.isShowCharset(newValue)) {
                charset.setVisible(true);
                charset.setManaged(true);
            } else {
                charset.setVisible(false);
                charset.setManaged(false);
            }
            if (MessageConvertFactory.isShowProtobuf(newValue)) {
                protobuf.setVisible(true);
                protobuf.setManaged(true);
            } else {
                protobuf.setVisible(false);
                protobuf.setManaged(false);
            }
        });
        box.getChildren().addAll(valueType, charset, protobuf);
        TitledPane pane = new TitledPane("value", box);
        pane.setCollapsible(false);
        return pane;
    }

    private Tile protobufForm(SimpleStringProperty type, SimpleStringProperty value) {
        Tile tile = new Tile(SettingClient.bundle().getString("form.topic.setting.protobuf"), "");
        Button select = new Button(
                "", new FontIcon(AntDesignIconsOutlined.FILE)
        );
        select.setCursor(Cursor.DEFAULT);
        CustomTextField folder = new CustomTextField();
        FontIcon clear = new FontIcon(AntDesignIconsFilled.CLOSE_CIRCLE);
        clear.setCursor(Cursor.DEFAULT);
        clear.getStyleClass().add(Styles.DANGER);
        clear.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            folder.setText("");
        });
        folder.setRight(clear);
        folder.setEditable(false);
        folder.setText(value.getValue());
        select.setOnAction(event -> {
                    FileChooser chooser = new FileChooser();
                    chooser.setTitle(SettingClient.bundle().getString("chooser.select"));
                    File directory = new File(Paths.documents());
                    if (StringUtils.hasText(folder.getText())) {
                        File current = new File(folder.getText());
                        if (current.exists()) {
                            directory = current.getParentFile();
                        }
                    }
                    chooser.setInitialDirectory(directory);
                    File file = chooser.showOpenDialog(State.stage());
                    if (file != null) {
                        folder.setText(file.getAbsolutePath());
                    }
                }

        );
        InputGroup group = new InputGroup(select, folder);
        group.setPrefWidth(WIDTH);
        folder.setPrefWidth(group.getPrefWidth() - select.getWidth());
        tile.setAction(group);
        tile.setActionHandler(folder::requestFocus);
        value.bind(folder.textProperty());
        if (MessageConvertFactory.isShowProtobuf(type.getValue())) {
            tile.setVisible(true);
            tile.setManaged(true);
        } else {
            tile.setVisible(false);
            tile.setManaged(false);
        }
        return tile;
    }


    private Tile typeForm(SimpleStringProperty value) {
        Tile tile = new Tile(SettingClient.bundle().getString("form.topic.setting.type"), "");
        ComboBox<String> select = new ComboBox<>();
        select.setValue(value.getValue());
        select.setItems(FXCollections.observableArrayList(MessageConvertFactory.types()));
        select.setPrefWidth(WIDTH);
        tile.setAction(select);
        tile.setActionHandler(select::requestFocus);
        value.bind(select.valueProperty());
        return tile;
    }

    private @NotNull TitledPane getKeyPane() {
        VBox box = new VBox();
        box.setSpacing(10);
        Tile keyType = typeForm(property.keyTypeProperty());
        Tile charset = charsetForm(property.keyTypeProperty(), property.keyCharsetProperty());
        Tile protobuf = protobufForm(property.keyTypeProperty(), property.keyProtobufFileProperty());
        property.keyTypeProperty().addListener((observable, oldValue, newValue) -> {
            if (MessageConvertFactory.isShowCharset(newValue)) {
                charset.setVisible(true);
                charset.setManaged(true);
            } else {
                charset.setVisible(false);
                charset.setManaged(false);
            }
            if (MessageConvertFactory.isShowProtobuf(newValue)) {
                protobuf.setVisible(true);
                protobuf.setManaged(true);
            } else {
                protobuf.setVisible(false);
                protobuf.setManaged(false);
            }
        });
        box.getChildren().addAll(keyType, charset, protobuf);
        TitledPane pane = new TitledPane("key", box);
        pane.setCollapsible(false);
        return pane;
    }


    private Tile charsetForm(SimpleStringProperty key, SimpleStringProperty value) {
        Tile tile = new Tile(SettingClient.bundle().getString("form.topic.setting.charset"), "");
        ComboBox<String> select = new ComboBox<>();
        select.setValue(value.getValue());
        select.setItems(FXCollections.observableArrayList(MessageConvertFactory.charsets()));
        select.setPrefWidth(WIDTH);
        tile.setAction(select);
        tile.setActionHandler(select::requestFocus);
        value.bind(select.valueProperty());
        if (MessageConvertFactory.isShowCharset(key.getValue())) {
            tile.setVisible(true);
            tile.setManaged(true);
        } else {
            tile.setVisible(false);
            tile.setManaged(false);
        }
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
            TopicSettingTask task = new TopicSettingTask(this.property);
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
