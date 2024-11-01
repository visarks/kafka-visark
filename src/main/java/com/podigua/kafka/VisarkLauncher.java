package com.podigua.kafka;

import com.podigua.path.Paths;
import javafx.application.Application;

/**
 * VISARK 启动器
 *
 * @author podigua
 * @date 2024/10/31
 */
public class VisarkLauncher {

    public static void main(String[] args) {
        Paths.identifier("Kafka-Visark");
        System.setProperty("LOG_PATH", Paths.appLog());
        State.args = args;
        Application.launch(VisakApplication.class, args);
    }
}
