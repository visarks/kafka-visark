package com.podigua.kafka;

import javafx.application.HostServices;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * 状态
 *
 * @author podigua
 * @time 2024/04/01
 */
public class State {
    public static final String PRODUCT="Kafka-Visark";
    public static final String VERSION="v1.1.8";
    public static final String URL = "https://www.visark.cn/store/releases/kafka-visark";
//    public static final String URL = "https://101.126.134.148/store/releases/kafka-visark";
    static HostServices hostServices;
    static Stage stage;
    static Pane pane;
    static String[] args;
    public static Pane pane() {
        return pane;
    }
    public static Stage stage() {
        return stage;
    }

    public static HostServices hostServices(){
        return hostServices;
    }

    public static String[] args() {
        return args;
    }
}
