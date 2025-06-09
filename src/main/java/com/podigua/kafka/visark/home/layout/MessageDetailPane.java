package com.podigua.kafka.visark.home.layout;

import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.podigua.kafka.core.utils.ClipboardUtils;
import com.podigua.kafka.core.utils.MessageUtils;
import com.podigua.kafka.visark.home.entity.Message;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

/**
 * 消息详细信息窗格
 *
 * @author podigua
 * @date 2024/05/08
 */
public class MessageDetailPane extends VBox {
    private static ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private final Message message;

    public MessageDetailPane(Message message) {
        this.message = message;
        VBox left = new VBox();
        add2Left(left);
        VBox right = new VBox();
        add2Right(right);
        VBox.setVgrow(right, Priority.ALWAYS);
        HBox top = new HBox();
        top.getChildren().addAll(left, right);
        left.prefWidthProperty().bind(top.widthProperty().divide(2));
        right.prefWidthProperty().bind(top.widthProperty().divide(2));
        VBox content = new VBox();
        this.getChildren().add(top);
        HBox tools = new HBox();
        setTools(tools, content);
        this.getChildren().addAll(tools, content);
        this.setPrefSize(970, 580);
        VBox.setVgrow(content, Priority.ALWAYS);
    }

    private void setTools(HBox tools, VBox content) {
        tools.setAlignment(Pos.CENTER_RIGHT);
        tools.setPadding(new Insets(10));
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.<String>observableArrayList("string", "json"));
        comboBox.getSelectionModel().select(0);
        String text = this.message.value().get();
        TextArea value = new TextArea(text);
        value.setWrapText(true);
        value.setEditable(false);
        comboBox.setOnAction(event -> {
            String item = comboBox.getSelectionModel().getSelectedItem();
            if (item != null) {
                if ("string".equals(item)) {
                    value.setText(text);
                } else if ("json".equals(item)) {
                    try {
                        if (text.startsWith("[") || text.startsWith("{")) {
                            JsonNode node = MAPPER.readTree(text);
                            value.setText(MAPPER.writeValueAsString(node));
                        } else {
                            throw new RuntimeException("e");
                        }
                    } catch (Exception e) {
                        value.setText(text);
                        comboBox.getSelectionModel().select(0);
                    }
                }
            }
        });
        comboBox.setValue("json");
        comboBox.fireEvent(new ActionEvent());
        Button button = new Button(null, new FontIcon(Material2AL.CONTENT_COPY));
        button.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_ICON, Styles.ACCENT);
        button.setOnAction(e -> {
            if (ClipboardUtils.copy(value.getText())) {
                MessageUtils.success(SettingClient.bundle().getString("copy.success"));
            }
        });
        tools.getChildren().addAll(comboBox, button);
        VBox.setVgrow(value, Priority.ALWAYS);
        content.getChildren().add(value);
    }

    private void add2Right(VBox right) {
        right.setPadding(new Insets(10));
        right.setSpacing(5);
        Tile partition = new Tile("partition:", "");
        partition.setAction(textField(this.message.partition().get() + ""));
        Tile offset = new Tile("offset:", "");
        offset.setAction(textField(this.message.offset().get() + ""));
        right.getChildren().addAll(partition, offset);
    }

    private void add2Left(VBox left) {
        left.setPadding(new Insets(10));
        left.setSpacing(5);
        Tile topic = new Tile("topic:", "");
        topic.setAction(textField(this.message.topic().get()));
        Tile timestamp = new Tile("timestamp:", "");
        timestamp.setAction(textField(this.message.timestamp().get()));
        Tile key = new Tile("key:", "");
        key.setAction(textField(this.message.key().get()));
        left.getChildren().addAll(topic, timestamp, key);
    }

    private TextField textField(String value) {
        TextField result = new TextField(value);
        result.setPrefWidth(300);
        return result;
    }

}
