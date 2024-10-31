package com.podigua.kafka.core.handler;

import com.podigua.kafka.visark.setting.SettingClient;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import org.slf4j.Logger;

/**
 *
 **/
public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Stage stage;

    public DefaultExceptionHandler(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        var dialog = createExceptionDialog(e);
        if (dialog != null) {
            dialog.showAndWait();
        }
    }

    private Alert createExceptionDialog(Throwable throwable) {
        Objects.requireNonNull(throwable);
        throwable.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(SettingClient.bundle().getString("alert.error.title"));
        alert.setHeaderText(null);
        alert.setContentText(throwable.getMessage());
        try (StringWriter sw = new StringWriter(); PrintWriter printWriter = new PrintWriter(sw)) {
            throwable.printStackTrace(printWriter);
            var label = new Label(SettingClient.bundle().getString("alert.error.title.stacktrace"));
            TextArea textArea = new TextArea(sw.toString());
            textArea.setEditable(false);
            textArea.setWrapText(false);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            VBox content = new VBox(5, label, textArea);
            content.setMaxWidth(Double.MAX_VALUE);
            alert.getDialogPane().setExpandableContent(content);
            alert.initOwner(stage);
            return alert;
        } catch (IOException e) {

            return null;
        }
    }
}
