package com.podigua.kafka.visark.home.chart;

import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;

/**
 * 指标图表
 *
 * @author podigua
 * @date 2024/10/25
 */
public interface MetricChart {
    /**
     * 名字
     *
     * @return {@link String }
     */
    String name();

    /**
     * ID
     *
     * @return {@link String }
     */
    String group();

    /**
     * 添加值
     *
     * @param name  名字
     * @param value 价值
     */
    <T extends Metric> void addValue(MetricName name, Metric value);
}
