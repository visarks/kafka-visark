package com.podigua.kafka.visark.settings.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class TopicSettingController implements Initializable {

    public Button cancelButton;
    public Button saveButton;
    public VBox formPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HBox box = new HBox();
        TitledPane key = new TitledPane("key", box);

        formPane.getChildren().add(key);
    }

    public void onCancel(ActionEvent actionEvent) {

    }

    public void onSave(ActionEvent actionEvent) {

    }
}
