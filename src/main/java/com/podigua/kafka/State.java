package com.podigua.kafka;

import javafx.stage.Stage;

/**
 * 状态
 *
 * @author podigua
 * @time 2024/04/01
 */
public class State {
    static Stage stage;
    static String[] args;

    public static Stage stage() {
        return stage;
    }

    public static String[] args() {
        return args;
    }
}
