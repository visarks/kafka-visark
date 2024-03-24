package com.podigua.kafka.visark.home.layout;

import atlantafx.base.theme.Tweaks;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.apache.kafka.common.TopicPartition;

import java.util.List;

/**
 * 使用者主题分区窗格
 *
 * @author podigua
 * @date 2024/03/24
 */
public class ConsumerTopicPartitionPane extends VBox {
    private final TableView<TopicPartition> tableView = new TableView<>();

    public ConsumerTopicPartitionPane() {
        TableColumn<TopicPartition, String> topic = new TableColumn<>("Topic");
        topic.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().topic()));

        TableColumn<TopicPartition, String> partition = new TableColumn<>("Partition");
        partition.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().partition() + ""));
        partition.setPrefWidth(80);

        topic.prefWidthProperty().bind(tableView.widthProperty().subtract(partition.prefWidthProperty()).subtract(10));
        tableView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE);
        tableView.getColumns().addAll(topic, partition);
        this.getChildren().add(tableView);
        this.setPrefSize(300,200);
    }

    public void reset(List<TopicPartition> topicPartitions) {
        tableView.getItems().clear();
        tableView.getItems().addAll(topicPartitions);
    }
}
