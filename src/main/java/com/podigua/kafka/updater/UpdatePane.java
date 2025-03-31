package com.podigua.kafka.updater;

import atlantafx.base.controls.Notification;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import com.podigua.kafka.core.event.NoticeCloseEvent;
import com.podigua.kafka.core.utils.MessageUtils;
import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class UpdatePane extends BorderPane {
    private final Releases releases;
    private final Tile tile = new Tile();
    private DownloadProgressBar downloadProgress;
    private final UpdateTask task;
    private final Label label = new Label();
    private Stage stage;

    public UpdatePane(Releases releases, Platform platform) {
        this.releases = releases;
        this.task = new UpdateTask(platform);
        this.setPrefSize(320, 180);
        center();
        setBottom();
        onSuccess();
    }

    private void onSuccess() {
        this.task.setOnSucceeded(event -> {
            this.stage.close();
            File file= (File) event.getSource().getValue();
            Button install = new Button(SettingClient.bundle().getString("updater.install"));
            install.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
            Button cancel = new Button(SettingClient.bundle().getString("updater.cancel"));
            cancel.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
            Notification notice = MessageUtils.success(SettingClient.bundle().getString("download.success"), Duration.ZERO, install, cancel);
            install.setOnAction(e->{
                new NoticeCloseEvent(notice).publish();
                Desktop.getDesktop().browseFileDirectory(file);
                System.exit(0);
            });
            cancel.setOnAction(e-> new NoticeCloseEvent(notice).publish());
        });
    }

    private void setBottom() {
        HBox box = gethBox();
        Button download = new Button(SettingClient.bundle().getString("updater.download"));
        Button cancel = new Button(SettingClient.bundle().getString("updater.cancel"));
        download.setOnAction(e -> {
            box.getChildren().clear();
            box.getChildren().add(cancel);
            download.setVisible(false);
            cancel.setVisible(true);
            downloadProgress.setVisible(true);
            new Thread(task).start();
        });
        cancel.setVisible(false);
        cancel.setOnAction(event -> {
            task.cancel();
            this.stage.close();
        });
        download.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        cancel.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.DANGER);
        box.getChildren().addAll(download);
        this.setBottom(box);
    }


    private void center() {
        VBox root = new VBox();
        tile.setTitle(SettingClient.bundle().getString("latest.version") + " " + releases.getVersion());
        tile.setDescription(releases.getNotes());
        ProgressBar progress = new ProgressBar();
        Label message = new Label();
        this.downloadProgress = new DownloadProgressBar(progress, message);
        this.downloadProgress.setVisible(false);
        this.task.progressProperty().addListener((observable, oldValue, newValue) -> progress.setProgress(newValue.doubleValue()));
        this.task.messageProperty().addListener((observable, oldValue, newValue) -> message.setText(newValue));
        downloadProgress.prefWidthProperty().bind(root.prefWidthProperty());
        root.setStyle("fx-spacing: 10px;");
        root.getChildren().addAll(tile, downloadProgress, label);
        this.setCenter(root);
    }

    private static HBox gethBox() {
        HBox box = new HBox();
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(5, 0, 5, 0));
        return box;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }
}
