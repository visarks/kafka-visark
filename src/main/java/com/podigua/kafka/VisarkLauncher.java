package com.podigua.kafka;

import javafx.application.Application;

/**
 *
 **/
public class VisarkLauncher {
    public static void main(String[] args) {
        State.args = args;
        Application.launch(VisakApplication.class, args);
    }
}
