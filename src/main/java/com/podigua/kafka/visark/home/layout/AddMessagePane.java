package com.podigua.kafka.visark.home.layout;

import atlantafx.base.theme.Styles;
import com.podigua.kafka.admin.Admin;
import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.core.utils.MessageUtils;
import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * 添加消息窗格
 *
 * @author podigua
 * @date 2024/06/24
 */
public class AddMessagePane extends BorderPane {
    private final String clusterId;
    private final String topic;
    private final KafkaProducer<String, String> producer;
    private final TextField key = new TextField("");
    private final TextArea value = new TextArea("");

    public AddMessagePane(String clusterId, String topic) {
        this.setPrefSize(800, 500);
        this.clusterId = clusterId;
        this.topic = topic;
        ClusterProperty property = AdminManger.property(this.clusterId);
        Admin admin = new Admin(property);
        Properties properties = admin.properties();
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(properties);
        VBox top = new VBox();
        top.getChildren().add(key);
        this.setTop(top);
        VBox center = new VBox();
        center.setPadding(new Insets(5, 0, 5, 0));
        VBox.setVgrow(value, Priority.ALWAYS);
        center.getChildren().add(value);
        this.setCenter(center);
        footer();
    }


    public void footer() {
        Button send = new Button();
        send.getStyleClass().addAll(Styles.BUTTON_OUTLINED,Styles.ACCENT);
        FontIcon icon = new FontIcon(Material2MZ.SHARE);
        send.setGraphic(icon);
        icon.setText(SettingClient.bundle().getString("message.send"));
        send.setOnAction(event -> {
            String key = this.key.getText();
            String text = this.value.getText();
            if (StringUtils.hasText(text)) {
                ProducerRecord<String, String> record = new ProducerRecord<>(this.topic, key, text);
                this.producer.send(record, (metadata, exception) -> Platform.runLater(()->{
                    if (exception == null) {
                        MessageUtils.success(SettingClient.bundle().getString("message.send.success"), Duration.millis(200));
                    } else {
                        throw new RuntimeException(exception);
                    }
                }));
            }
        });
        HBox box = new HBox();
        box.setSpacing(10);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER_RIGHT);
        box.getChildren().addAll(send);
        this.setBottom(box);
    }
}
