package com.podigua.kafka.visark.home.layout;

import com.podigua.kafka.core.utils.NodeUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;


/**
 * 基本刷新窗格
 *
 * @author podigua
 * @date 2024/03/24
 */
public abstract class BaseRefreshPane extends BorderPane {
    /**
     * 集群 ID
     */
    private final String clusterId;
    /**
     * 关闭
     */
    private final Button close = NodeUtils.close();

    protected BaseRefreshPane(String clusterId) {
        this.clusterId = clusterId;
        addBottom();
    }

    /**
     * 集群 ID
     *
     * @return {@link String}
     */
    protected String clusterId() {
        return clusterId;
    }


    protected void addBottom() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setSpacing(10);
        Button refresh = NodeUtils.refresh();
        refresh.setOnAction(event -> reload());
        box.getChildren().addAll(close, refresh);
        this.setBottom(box);
    }

    protected abstract void reload();

    /**
     * 设置为关闭
     *
     * @param value 价值
     */
    public void setOnClose(EventHandler<ActionEvent> value) {
        close.setOnAction(value);
    }
}
