package com.podigua.kafka.core.handler;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

/**
 *
 **/
public class DefaultExceptionHandler  implements Thread.UncaughtExceptionHandler {
    private final Stage stage;

    public DefaultExceptionHandler(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();

        var dialog = createExceptionDialog(e);
        if (dialog != null) {
            dialog.showAndWait();
        }
    }

    private Alert createExceptionDialog(Throwable throwable) {
        Objects.requireNonNull(throwable);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误信息");
        alert.setHeaderText(null);
        alert.setContentText(throwable.getMessage());
        try (StringWriter sw = new StringWriter(); PrintWriter printWriter = new PrintWriter(sw)) {
            throwable.printStackTrace(printWriter);
            TextArea textArea = new TextArea(sw.toString());
            textArea.setEditable(false);
            textArea.setWrapText(false);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            VBox content = new VBox(5, textArea);
            content.setMaxWidth(Double.MAX_VALUE);
            alert.getDialogPane().setExpandableContent(content);
            alert.initOwner(stage);
            return alert;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
