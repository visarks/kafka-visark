package com.podigua.kafka.visark.home.control;

import com.podigua.kafka.core.unit.DataSize;
import com.podigua.kafka.core.utils.NumberUtils;
import com.podigua.kafka.excel.ExcelUtils;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.math.BigDecimal;


public class MemoryBar extends Pane {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
    private final Timeline schedule = new Timeline(new KeyFrame(Duration.seconds(3)));
    private final SimpleObjectProperty<DataSize> headCommited = new SimpleObjectProperty<>(DataSize.ofBytes(0));
    private final SimpleObjectProperty<DataSize> headMax = new SimpleObjectProperty<>(DataSize.ofBytes(0));
    private final SimpleObjectProperty<DataSize> headInit = new SimpleObjectProperty<>(DataSize.ofBytes(0));
    private final SimpleObjectProperty<DataSize> headUsed = new SimpleObjectProperty<>(DataSize.ofBytes(0));
    private final SimpleObjectProperty<DataSize> nonHeadCommited = new SimpleObjectProperty<>(DataSize.ofBytes(0));
    private final SimpleObjectProperty<DataSize> nonHeadInit = new SimpleObjectProperty<>(DataSize.ofBytes(0));
    private final SimpleObjectProperty<DataSize> nonHeadUsed = new SimpleObjectProperty<>(DataSize.ofBytes(0));
    private final SimpleObjectProperty<DataSize> jvmFree = new SimpleObjectProperty<>(DataSize.ofBytes(0));
    private final SimpleObjectProperty<DataSize> jvmTotal = new SimpleObjectProperty<>(DataSize.ofBytes(0));
    private final SimpleObjectProperty<DataSize> jvmMax = new SimpleObjectProperty<>(DataSize.ofBytes(0));
    private final SimpleObjectProperty<DataSize> used = new SimpleObjectProperty<>(DataSize.ofBytes(0));
    private final SimpleObjectProperty<DataSize> total = new SimpleObjectProperty<>(DataSize.ofBytes(0));
    private final SimpleObjectProperty<BigDecimal> rate = new SimpleObjectProperty<>(BigDecimal.ZERO);

    private final VBox pane = new VBox();
    private final MemoryProgressBar progress = new MemoryProgressBar();

    public MemoryBar() {
        this.progress.setProgress(0);
        this.pane.setStyle("-fx-background-color: -color-bg-inset;");
        this.pane.setVisible(false);
        this.pane.setPrefWidth(150);
        this.pane.setMinWidth(150);
        this.pane.setPrefHeight(270);
        this.pane.setTranslateY(-273);
        this.pane.setTranslateX(-33);
        this.setPrefHeight(30);
        this.setMaxWidth(120);
        this.setPrefSize(120, 30);
        this.addEventHandler(MouseDragEvent.MOUSE_ENTERED, event -> {
            this.pane.setVisible(true);
        });
        AnchorPane.setBottomAnchor(this.pane, 30d);
        AnchorPane.setBottomAnchor(this.progress, 0d);
        AnchorPane.setTopAnchor(this.progress, 0d);
        AnchorPane.setLeftAnchor(this.progress, 0d);
        AnchorPane.setRightAnchor(this.progress, 0d);
        this.addEventHandler(MouseDragEvent.MOUSE_EXITED, event -> {
            this.pane.setVisible(false);
        });
        schedule.setOnFinished(e -> this.update());
        this.progress.setOnMouseClicked(event -> clear());
        this.getChildren().addAll(progress, pane);
        schedule.play();
        this.setMemory();
    }

    private void clear() {
        ManagementFactory.getMemoryMXBean().gc();
        this.schedule.stop();
        update();
    }

    private void setMemory() {
        this.pane.setPadding(new Insets(5));
        HBox head = group(SettingClient.bundle().getString("memory.head"));
        HBox headUsedTile = getItem(SettingClient.bundle().getString("memory.head.used"), headUsed);
        HBox headCommitedTile = getItem(SettingClient.bundle().getString("memory.head.commited"), headCommited);
        HBox headInitTile = getItem(SettingClient.bundle().getString("memory.head.init"), headInit);
        HBox headMaxTile = getItem(SettingClient.bundle().getString("memory.head.max"), headMax);
        HBox noneHead = group(SettingClient.bundle().getString("memory.non.head"));

        HBox nonHeadUsedTile = getItem(SettingClient.bundle().getString("memory.non.head.used"), nonHeadUsed);
        HBox nonHeadCommitedTile = getItem(SettingClient.bundle().getString("memory.non.head.commited"), nonHeadCommited);
        HBox nonHeadInitTile = getItem(SettingClient.bundle().getString("memory.non.head.init"), nonHeadInit);

        HBox jvm = group(SettingClient.bundle().getString("memory.jvm"));
        HBox jvmFreeTile = getItem(SettingClient.bundle().getString("memory.jvm.free"), jvmFree);

        this.pane.getChildren().addAll(head, headUsedTile, headCommitedTile, headInitTile, headMaxTile, noneHead,nonHeadUsedTile,nonHeadCommitedTile,nonHeadInitTile,jvm,jvmFreeTile);
        head.prefWidthProperty().bind(this.pane.prefWidthProperty());
    }

    /**
     * 获取项目
     *
     * @param name 名字
     * @param property 属性
     * @return {@link HBox }
     */
    private HBox getItem(String name, SimpleObjectProperty<DataSize> property) {
        Label label = new Label();
        Label title = new Label(name);
        label.setPrefWidth(50);
        label.setAlignment(Pos.CENTER_RIGHT);
        HBox root = new HBox(title, label);
        property.addListener((observable, oldValue, newValue) -> label.setText(newValue.toString()));
        root.setSpacing(10);
        root.setAlignment(Pos.CENTER_RIGHT);
        return root;
    }

    private static HBox group(String label) {
        HBox head = new HBox(new Label(label));
        head.setStyle("-fx-padding: 5px;-fx-font-weight: bold;-fx-fill: -color-fg-default");
        head.setAlignment(Pos.CENTER_LEFT);
        return head;
    }

    private void update() {
        MemoryUsage head = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        headCommited.set(DataSize.ofBytes(head.getCommitted()));
        headMax.set(DataSize.ofBytes(head.getMax()));
        headInit.set(DataSize.ofBytes(head.getInit()));
        headUsed.set(DataSize.ofBytes(head.getUsed()));
        MemoryUsage nonHead = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        nonHeadCommited.set(DataSize.ofBytes(nonHead.getCommitted()));
        nonHeadInit.set(DataSize.ofBytes(nonHead.getInit()));
        nonHeadUsed.set(DataSize.ofBytes(nonHead.getUsed()));
        jvmFree.set(DataSize.ofBytes(Runtime.getRuntime().freeMemory()));
        jvmMax.set(DataSize.ofBytes(Runtime.getRuntime().maxMemory()));
        jvmTotal.set(DataSize.ofBytes(Runtime.getRuntime().totalMemory()));
        used.set(DataSize.ofBytes(head.getUsed() + nonHead.getUsed()));
        total.set(DataSize.ofBytes(head.getCommitted() + nonHead.getCommitted()));
        rate.set(NumberUtils.divide(used.get().toBytes(), total.get().toBytes(), 2));
        this.progress.setProgress(rate.get().doubleValue());
        this.progress.setText(used.get().toMegabytes(0).toPlainString() + " / " + total.get().toMegabytes(0).toPlainString() + "M");
        schedule.play();
    }
}
