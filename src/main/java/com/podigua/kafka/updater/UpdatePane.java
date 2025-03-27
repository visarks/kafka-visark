package com.podigua.kafka.updater;

import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UpdatePane extends BorderPane {
    private final Releases releases;
    private final Tile tile = new Tile();

    public UpdatePane(Releases releases) {
        this.releases = releases;
        this.setPrefSize(320, 180);
//        top();
        center();
        setBottom();
    }

    private void setBottom() {
        HBox box = gethBox();
        Button button = new Button("立即更新");
        button.setOnAction(e-> Updater.download(releases));
        button.getStyleClass().addAll(Styles.BUTTON_OUTLINED,Styles.ACCENT);
        box.getChildren().add(button);
        this.setBottom(box);
    }


    private void center() {
        VBox root = new VBox();
        tile.setTitle(SettingClient.bundle().getString("latest.version") + " " + releases.getVersion());
        tile.setDescription(releases.getNotes());
        root.getChildren().add(tile);
        this.setCenter(root);
    }

    private static HBox gethBox() {
        HBox box = new HBox();
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(5, 0, 5, 0));
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
        this.setTop(root);
    }
}
