package com.podigua.kafka.updater;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class UpdaterProcessPane extends BorderPane {
    private final Platform platform;
    private ProgressBar bar = new ProgressBar(0);
    private final UpdateTask task;

    public UpdaterProcessPane(Platform platform) {
        this.platform = platform;
        this.task = new UpdateTask(platform);
        setCenter(buildCenter());
        setPrefSize(392, 164);
        new Thread(task).start();
    }

    private Node buildCenter() {
        HBox root = new HBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: red;");
        task.progressProperty().addListener((observable, oldValue, newValue) -> bar.setProgress(newValue.doubleValue()));
        root.getChildren().add(bar);
        bar.prefWidthProperty().bind(root.widthProperty());
        return root;
    }
}
