package com.podigua.kafka.core.utils;

import atlantafx.base.theme.Styles;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

/**
 * 节点工具类
 *
 * @author podigua
 * @date 2024/03/23
 */
public class NodeUtils {

    /**
     * 加载中
     *
     * @return {@link ProgressIndicator}
     */
    public static ProgressIndicator progress() {
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(12, 12);
        progress.setStyle("-fx-background-color: -color-accent-subtle");
        return progress;
    }

    /**
     * 设置锚点
     *
     * @param node  节点
     * @param value 价值
     */
    public static void setAnchor(Node node, double value) {
        AnchorPane.setTopAnchor(node, value);
        AnchorPane.setRightAnchor(node, value);
        AnchorPane.setBottomAnchor(node, value);
        AnchorPane.setLeftAnchor(node, value);
    }

    /**
     * 刷新
     *
     * @return {@link Button}
     */
    public static Button refresh() {
        Button result = new Button(SettingClient.bundle().getString("refresh"));
        result.setGraphic(new FontIcon(Material2MZ.REFRESH));
        result.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        return result;
    }

    /**
     * 关闭
     *
     * @return {@link Button}
     */
    public static Button close() {
        Button result = new Button(SettingClient.bundle().getString("form.close"));
        result.setGraphic(new FontIcon(Material2AL.CLOSE));
        result.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.DANGER);
        return result;
    }
}
