package com.podigua.kafka.visark.home.layout;

import com.podigua.kafka.admin.AdminManger;
import com.podigua.kafka.admin.ConsumerOffset;
import com.podigua.kafka.admin.task.QueryConsumerOffsetTask;
import com.podigua.kafka.visark.home.chart.ConsumerTagOffsetChart;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.common.Node;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * 主页
 *
 * @author podigua
 * @date 2024/10/25
 */
public class MetricClusterPane extends ContentBorderPane {
    private static final Integer DEFAULT_TIME=1;
    private final ClusterNode value;
    private Timeline timeline;
    private final ObservableList<RefreshTime> refreshList = FXCollections.observableArrayList(
            new RefreshTime(-1, "None"),
            new RefreshTime(DEFAULT_TIME, "1m"),
            new RefreshTime(5, "5m"),
            new RefreshTime(10, "10m"),
            new RefreshTime(30, "30m")
    );
    private ObservableList<ConsumerTagOffsetChart> charts = FXCollections.observableArrayList();
    private Map<String, ConsumerTagOffsetChart> metrics = new LinkedHashMap<>();

    public MetricClusterPane(ClusterNode value) {
        this.value = value;
        initHeader();
        initChart();
        startTimer();
        FlowPane center = new FlowPane();
        ScrollPane scroll = new ScrollPane(center);
        center.prefWidthProperty().bind(scroll.widthProperty().subtract(10));
        center.setVgap(8);
        center.setHgap(4);
        for (Chart chart : this.charts) {
            chart.prefWidthProperty().bind(center.prefWidthProperty().divide(3).subtract(5));
            center.getChildren().add(chart);
        }
        this.setCenter(scroll);
    }

    private void initChart() {
        KafkaAdminClient client = AdminManger.get(this.value.clusterId());
        try {
            Collection<ConsumerGroupListing> listings = client.listConsumerGroups().all().get();
            for (ConsumerGroupListing listing : listings) {
                String groupId = listing.groupId();
                ConsumerTagOffsetChart chart = new ConsumerTagOffsetChart(new CategoryAxis(), new NumberAxis(), groupId);
                this.charts.add(chart);
                this.metrics.put(groupId, chart);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void initHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER_RIGHT);
        ComboBox<RefreshTime> list = new ComboBox<>();
        list.setPrefWidth(200);
        list.setItems(this.refreshList);
        list.getSelectionModel().select(1);
        list.getSelectionModel().selectedItemProperty().addListener((e, o, n) -> {
            timeline.stop();
            if (n != null && n.value > 0) {
                timeline.getKeyFrames().clear();
                timeline.getKeyFrames().add(new KeyFrame(Duration.minutes(n.value), event -> metrics()));
                timeline.play();
            }
        });
        header.getChildren().add(list);
        this.setTop(header);
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.minutes(DEFAULT_TIME), event -> {
            metrics();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        metrics();
    }

    private void metrics() {
        this.metrics.forEach((groupId, chart) -> {
            QueryConsumerOffsetTask task = new QueryConsumerOffsetTask(this.value.clusterId(), groupId);
            task.setOnSucceeded(event -> {
                try {
                    List<ConsumerOffset> offsets = task.get();
                    Platform.runLater(()->{
                        chart.addData(offsets);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            Thread.ofVirtual().start(task);
        });

    }


    @Override
    public ClusterNode value() {
        return this.value;
    }

    @Override
    public void close() {
        this.timeline.stop();
    }

    public static class RefreshTime {
        private final Integer value;
        private final String label;


        public RefreshTime(Integer value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public String toString() {
            return this.label;
        }
    }

}
