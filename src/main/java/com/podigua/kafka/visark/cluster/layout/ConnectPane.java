package com.podigua.kafka.visark.cluster.layout;

import atlantafx.base.controls.RingProgressIndicator;
import atlantafx.base.theme.Styles;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

/**
 * “连接中”窗格
 *
 * @author podigua
 * @date 2024/03/22
 */
public class ConnectPane extends BorderPane {
    public ConnectPane(EventHandler<ActionEvent> value) {
        setCenter(center());
        setBottom(getBottom(value));
        this.setPrefSize(350, 130);
    }

    private static HBox getBottom(EventHandler<ActionEvent> value) {
        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER);
        Button button = new Button(SettingClient.bundle().getString("form.cancel"));
        button.setGraphic(new FontIcon(Material2AL.CANCEL));
        button.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.TEXT);
        button.setOnAction(value);
        bottom.getChildren().add(button);
        return bottom;
    }

    private HBox center() {
        HBox center = new HBox();
        center.setSpacing(10);
        center.setAlignment(Pos.CENTER);
        var ring = new RingProgressIndicator();
        ring.setMinSize(20, 20);
        center.getChildren().addAll(ring, new Label(SettingClient.bundle().getString("cluster.connecting")));
        return center;
    }
}
