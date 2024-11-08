package com.podigua.kafka;

import javafx.application.HostServices;
import javafx.stage.Stage;

/**
 * 状态
 *
 * @author podigua
 * @time 2024/04/01
 */
public class State {
    public static String PRODUCT="Kafka-Visark";
    public static String VERSION="1.1.6";
    static HostServices hostServices;
    static Stage stage;
    static String[] args;

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
