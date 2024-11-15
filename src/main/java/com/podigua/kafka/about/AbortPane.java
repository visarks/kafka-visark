package com.podigua.kafka.about;

import com.podigua.kafka.State;
import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Abort 窗格
 *
 * @author podigua
 * @date 2024/11/08
 */
public class AbortPane extends BorderPane {
    public AbortPane() {
        this.setPrefSize(367, 200);
        top();
        center();
    }


    private void center() {
        VBox root = new VBox();
        Label title = new Label("Kafka Visark "+ State.VERSION);
        title.setStyle("-fx-font-size: 20;-fx-font-weight: blod;");
        Label information = new Label(SettingClient.bundle().getString("product.information"));
        Label copyright = new Label("Copyright © 2024");
        HBox titleBox = gethBox();
        titleBox.getChildren().add(title);
        HBox informationBox = gethBox();
        informationBox.getChildren().add(information);
        HBox copyrightBox = gethBox();
        copyrightBox.getChildren().add(copyright);
        root.getChildren().addAll(titleBox, informationBox, copyrightBox);
//        root.setStyle("-fx-background-color: -color-bg-inset;");
        this.setCenter(root);
    }

    private static HBox gethBox() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(5,0,5,0));
//        box.setStyle("-fx-background-color: -color-bg-inset;");
        return box;
    }

    private void top() {
        HBox root = gethBox();
        root.setPadding(new Insets(20));
        Image image = new Image(Resources.getResource("/images/logo.png").toExternalForm());
        ImageView view = new ImageView(image);
        view.setFitHeight(46);
        view.setFitWidth(46);
        root.getChildren().add(view);
//        root.setStyle("-fx-background-color: -color-bg-inset;");
        this.setTop(root);
    }
}
