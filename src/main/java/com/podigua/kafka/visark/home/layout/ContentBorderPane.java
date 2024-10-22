package com.podigua.kafka.visark.home.layout;

import com.podigua.kafka.visark.home.entity.ClusterNode;
import javafx.scene.layout.BorderPane;

/**
 *
 **/
public  abstract class ContentBorderPane extends BorderPane {
    public abstract ClusterNode value();
    public abstract void close();
}
