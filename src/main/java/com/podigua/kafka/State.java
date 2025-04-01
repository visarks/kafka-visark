package com.podigua.kafka;

import com.podigua.kafka.license.License;
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
    public static final String VERSION="1.1.8";
    static HostServices hostServices;
    static Stage stage;
    static Pane pane;
    static License license;
    static String[] args;
    public static License license() {
        return license;
    }
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
