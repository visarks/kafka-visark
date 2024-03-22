package com.podigua.kafka.core.utils;

import com.podigua.kafka.State;
import com.podigua.kafka.core.CardHeaderPane;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.Optional;

/**
 * alert 工具类
 *
 * @author podigua
 * @date 2024/03/21
 */
public class AlertUtils {
    /**
     * 确认
     *
     * @param content 内容
     * @return {@link Optional}<{@link ButtonType}>
     */
    public static Optional<ButtonType> confirm(String content) {
        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add(Resources.getResource("/css/main.css").toExternalForm());
        CardHeaderPane header = new CardHeaderPane(null, SettingClient.bundle().getString("alert.title"), alert::close);
        AnchorPane box = new AnchorPane(header);
        AnchorPane.setLeftAnchor(header, 10.0);
        AnchorPane.setTopAnchor(header, 5.0);
        AnchorPane.setRightAnchor(header, 0.0);
        AnchorPane.setBottomAnchor(header, 10.0);
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

    /**
     * 错误
     *
     * @param parent  父母
     * @param content 内容
     */
    public static void error(Window parent, String content) {
        var alert = new Alert(Alert.AlertType.NONE,content,ButtonType.OK);
        alert.setTitle(SettingClient.bundle().getString("alert.error.title"));
        alert.setHeaderText(null);
        alert.initOwner(parent);
        alert.showAndWait();
    }
}
