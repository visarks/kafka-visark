package com.podigua.kafka.updater;

import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

public class DownloadProgressBar extends StackPane {
    private final ProgressBar progress;
    private final Label message;

    public DownloadProgressBar(ProgressBar progress, Label message) {
        super(progress, message);
        this.setPadding(new Insets(10, 0, 0, 0));
        this.progress = progress;
        this.message = message;
        this.progress.getStyleClass().add(Styles.LARGE);
        this.progress.prefWidthProperty().bind(this.widthProperty());
    }
}
