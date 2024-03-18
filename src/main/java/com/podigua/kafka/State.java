package com.podigua.kafka;

import javafx.stage.Stage;

/**
 *
 **/
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
