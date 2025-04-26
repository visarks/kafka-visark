package com.podigua.kafka.visark.home.control;

import atlantafx.base.theme.Styles;
import com.podigua.kafka.core.utils.Resources;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MemoryProgressBar extends StackPane {
    private final ProgressBar progress = new ProgressBar();
    private final Label message = new Label();

    public MemoryProgressBar() {
        this.progress.getStyleClass().add(Styles.LARGE);
        this.progress.prefWidthProperty().bind(this.widthProperty());
        this.progress.prefWidthProperty().bind(this.widthProperty());
        this.message.prefHeightProperty().bind(this.heightProperty());
        this.message.setAlignment(Pos.CENTER);
        this.getChildren().addAll(progress, message);
    }
    public void setText(String text) {
        this.message.setText(text);
    }

    public void setProgress(double progress) {
        this.progress.setProgress(progress);
    }

    public String getText() {
        return this.message.getText();
    }

    public double getProgress() {
        return this.progress.getProgress();
    }
}
