package com.podigua.kafka.visark.home.layout;

import atlantafx.base.controls.Tile;
import com.podigua.kafka.admin.task.QuerySingleConsumerTask;
import com.podigua.kafka.core.event.LoadingEvent;
import com.podigua.kafka.visark.home.entity.ConsumerDescription;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;

/**
 * 显示consumer 详情
 *
 * @author podigua
 * @date 2024/03/23
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
        this.prefHeight(200);
        addCenter();
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
        task.setOnFailed(event -> {
            LoadingEvent.STOP.publish();
        });
        new Thread(task).start();
    }

    private void addCenter() {
        GridPane grid=new GridPane(2,2);
        grid.setStyle("-fx-background-color: red;");
        grid.getRowConstraints().add(row1());
        grid.add(groupId(),0,0);
        grid.add(groupId(),0,1);
        grid.add(groupId(),1,0);
        this.setCenter(grid);
    }

    private RowConstraints row1() {
        RowConstraints result=new RowConstraints();

        return result;
    }

    private HBox groupId() {
        Tile group = new Tile();
        group.setTitle("groupId");
        Label label = new Label();
        this.description.groupIdProperty().addListener((e, o, n) -> {
            label.setText(n);
        });
        group.setAction(label);
        HBox result = new HBox(group);
        result.setStyle("-fx-background-color: blue;");
        return result;
    }


}
