package com.podigua.kafka.visark.home.chart;

import com.podigua.kafka.admin.ConsumerOffset;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import org.springframework.util.CollectionUtils;
import org.sqlite.date.DateFormatUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Network io-rate 图表
 *
 * @author podigua
 * @date 2024/10/25
 */
public class ConsumerTagOffsetChart extends AreaChart<String, Number> {
    private final CategoryAxis x;
    private final NumberAxis y;

    public ConsumerTagOffsetChart(CategoryAxis x, NumberAxis y, String group) {
        super(x, y);
        this.setTitle(group);
        this.x = x;
        this.y = y;
        this.setMaxHeight(300);
        this.setHover(true);
    }

    public void addData(List<ConsumerOffset> consumers) {
        if (!CollectionUtils.isEmpty(consumers)) {
            String key = DateFormatUtils.format(new Date(), "HH:mm:ss");
            Map<String, List<ConsumerOffset>> topics = consumers.stream().collect(Collectors.groupingBy(ConsumerOffset::topic));
            x.getCategories().add(key);
            ObservableList<Series<String, Number>> numbers = this.getData();
            topics.forEach((topic, list) -> {
                Series<String, Number> series = null;
                for (Series<String, Number> number : numbers) {
                    if (number.getName().equals(topic)) {
                        series = number;
                    }
                }
                if (series == null) {
                    series = new Series<>();
                    series.setName(topic);
                    this.getData().add(series);
                }
                long total = list.stream().mapToLong(o -> o.end() - o.offset()).sum();
                series.getData().add(new Data<>(key, total));
            });
        }
    }

}
