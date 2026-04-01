package com.podigua.kafka.visark.home.layout;

import atlantafx.base.controls.Tile;
import com.podigua.kafka.admin.task.QuerySingleConsumerTask;
import com.podigua.kafka.core.event.LoadingEvent;
import com.podigua.kafka.visark.home.entity.ConsumerDescription;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;

/**
 * 显示 Consumer 详情
 *
 * @author podigua
 */
public class ShowConsumerDetailPane extends BorderPane {
    /**
     * 集群 ID
     */
    private final String clusterId;
    /**
     * 组 ID
     */
    private final String groupId;

    private final ConsumerDescription description = new ConsumerDescription();

    public ShowConsumerDetailPane(String clusterId, String groupId) {
        this.clusterId = clusterId;
        this.groupId = groupId;
        this.setPrefHeight(200);
        initLayout();
        reload();
    }

    /**
     * 重新加载
     */
    protected void reload() {
        QuerySingleConsumerTask task = new QuerySingleConsumerTask(clusterId, groupId);
        task.setOnSucceeded(event -> {
            LoadingEvent.STOP.publish();
            try {
                ConsumerGroupDescription groupDescription = task.get();
                description.reset(groupDescription);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        task.setOnFailed(event -> LoadingEvent.STOP.publish());
        Thread.ofVirtual().start(task);
    }

    private void initLayout() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        // 列约束：两列等宽
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        // 第一行：GroupId 和 State
        grid.add(createTile("Group ID", description.groupIdProperty()), 0, 0);
        grid.add(createTile("State", description.stateProperty()), 1, 0);

        // 第二行：Coordinator 和 Partition Assignor
        grid.add(createTile("Coordinator", description.coordinatorProperty()), 0, 1);
        grid.add(createTile("Partition Assignor", description.partitionAssignorProperty()), 1, 1);

        // 第三行：Simple Consumer Group 标识
        Tile simpleTile = new Tile("Simple Consumer Group", "");
        Label simpleLabel = new Label();
        description.isSimpleConsumerGroupProperty().addListener((obs, old, newVal) -> {
            simpleLabel.setText(Boolean.TRUE.equals(newVal) ? "Yes" : "No");
            simpleLabel.setStyle(Boolean.TRUE.equals(newVal)
                    ? "-fx-text-fill: #4CAF50; -fx-font-weight: bold;"
                    : "-fx-text-fill: #9E9E9E;");
        });
        simpleTile.setAction(simpleLabel);
        grid.add(simpleTile, 0, 2);

        this.setCenter(grid);
//        this.setStyle("-fx-background-color: #fafafa; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8;");
    }

    /**
     * 创建 Tile 组件
     */
    private Tile createTile(String title, javafx.beans.property.StringProperty property) {
        Tile tile = new Tile(title, "");
        Label label = new Label();
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        property.addListener((obs, old, newVal) -> label.setText(newVal != null ? newVal : "-"));
        tile.setAction(label);
        return tile;
    }
}
