package com.podigua.kafka.visark.home.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 * 全部详细信息
 *
 * @author podigua
 * @date 2024/03/26
 */
public class TotalDetails {
    /**
     * 分区
     */
    private final SimpleIntegerProperty partitions = new SimpleIntegerProperty(0);
    /**
     * 关闭启动
     */
    private final SimpleLongProperty offStart = new SimpleLongProperty(0);
    /**
     * 关闭结束
     */
    private final SimpleLongProperty offEnd = new SimpleLongProperty(0);
    /**
     * 消息
     */
    private final SimpleLongProperty messages = new SimpleLongProperty(0);


    public SimpleIntegerProperty partitions() {
        return partitions;
    }
    public SimpleLongProperty offStart() {
        return offStart;
    }
    public SimpleLongProperty offEnd() {
        return offEnd;
    }
    public SimpleLongProperty messages() {
        return messages;
    }

    public void clear() {
        this.messages().set(0);
        this.offEnd().set(0);
        this.offStart().set(0);
        this.partitions().set(0);
    }
}
