package com.podigua.kafka.core.utils;

import com.podigua.kafka.State;
import com.podigua.kafka.core.CardHeaderPane;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;

import java.util.Optional;

/**
 *
 **/
public class AlertUtils {
    public static Optional<ButtonType> confirm(String content) {
        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(Resources.getResource("/css/main.css").toExternalForm());
        CardHeaderPane header=new CardHeaderPane(null,"信息提示", alert::close);
        AnchorPane box=new AnchorPane(header);
        AnchorPane.setLeftAnchor(header,10.0);
        AnchorPane.setTopAnchor(header,5.0);
        AnchorPane.setRightAnchor(header,0.0);
        AnchorPane.setBottomAnchor(header,10.0);
        alert.getDialogPane().setHeader(box);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.setContentText(content);
        alert.initOwner(State.stage());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && ButtonType.OK.equals(result.get())) {
            return Optional.of(ButtonType.OK);
        } else {
            return Optional.empty();
        }
    }
}
