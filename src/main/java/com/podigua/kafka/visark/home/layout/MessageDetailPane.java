package com.podigua.kafka.visark.home.layout;

import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.podigua.kafka.visark.home.entity.Message;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * 消息详细信息窗格
 *
 * @author podigua
 * @date 2024/05/08
 */
public class MessageDetailPane extends VBox {
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
        value.setEditable(false);
        comboBox.setOnAction(event -> {
            String item = comboBox.getSelectionModel().getSelectedItem();
            if (item != null) {
                if ("string".equals(item)) {
                    value.setText(text);
                } else if ("json".equals(item)) {
                    try {
                        if (text.startsWith("[") || text.startsWith("{")) {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode node = mapper.readTree(text);
                            mapper.enable(SerializationFeature.INDENT_OUTPUT);
                            value.setText(mapper.writeValueAsString(node));
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
        tools.getChildren().add(comboBox);
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
