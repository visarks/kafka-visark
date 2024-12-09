package com.podigua.kafka.core.utils;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import com.podigua.kafka.core.event.NoticeCloseEvent;
import com.podigua.kafka.core.event.NoticeEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

/**
 * @author podigua
 * @date 2024/03/22
 */
public class MessageUtils {
    private final static double WIDTH = 350;

    /**
     * 显示成功消息
     *
     * @param message 消息
     */
    public static void success(String message) {
        success(message, Duration.seconds(3),null);
    }
    public static void success(String message,Duration duration) {
        success(message, duration,null);
    }
    /**
     * 显示成功消息
     *
     * @param message 消息
     */
    public static void success(String message, Button... buttons) {
        success(message, Duration.seconds(3),buttons);
    }

    /**
     * 显示消息
     *
     * @param message 消息
     */
    public static void warning(String message) {
        warning(message, Duration.seconds(3));
    }
    /**
     * 显示消息
     *
     * @param message 消息
     */
    public static void error(String message) {
        error(message, Duration.seconds(3));
    }

    /**
     * 显示成功消息
     *
     * @param message 消息
     */
    public static void success(String message, Duration duration,Button... buttons) {
        final var notice = new Notification(message, new FontIcon(Material2OutlinedAL.CHECK_CIRCLE_OUTLINE));
        notice.setPrefWidth(WIDTH);
        notice.getStyleClass().addAll(Styles.ACCENT, Styles.ELEVATED_1);
        AnchorPane.setTopAnchor(notice, 5.0);
        AnchorPane.setRightAnchor(notice, 5.0);
        notice.setOnClose(e -> {
            var out = Animations.slideOutRight(notice, Duration.millis(300));
            out.playFromStart();
            out.setOnFinished(event -> {
                new NoticeCloseEvent(notice).publish();
            });
        });
        if(buttons!=null){
            notice.setPrimaryActions(buttons);
        }
        new NoticeEvent(notice).duration(duration).publish();
    }

    /**
     * 显示错误消息
     *
     * @param message 消息
     */
    public static void error(String message, Duration duration) {
        final var notice = new Notification(message, new FontIcon(Material2OutlinedAL.ERROR));
        notice.setPrefWidth(WIDTH);
        notice.getStyleClass().addAll(Styles.DANGER, Styles.ELEVATED_1);
        AnchorPane.setTopAnchor(notice, 5.0);
        AnchorPane.setRightAnchor(notice, 5.0);
        notice.setOnClose(e -> {
            var out = Animations.slideOutRight(notice, Duration.millis(300));
            out.playFromStart();
            out.setOnFinished(event -> {
                new NoticeCloseEvent(notice).publish();
            });
        });
        new NoticeEvent(notice).duration(duration).publish();
    }

    public static void warning(String message, Duration duration) {
        Notification notice = new Notification(message, new FontIcon(Material2OutlinedAL.ASSIGNMENT));
        notice.setPrefWidth(WIDTH);
        notice.getStyleClass().addAll(Styles.WARNING, Styles.ELEVATED_1);
        AnchorPane.setTopAnchor(notice, 5.0);
        AnchorPane.setRightAnchor(notice, 5.0);
        notice.setOnClose(e -> {
            var out = Animations.slideOutRight(notice, Duration.millis(300));
            out.playFromStart();
            out.setOnFinished(event -> {
                new NoticeCloseEvent(notice).publish();
            });
        });
        new NoticeEvent(notice).duration(duration).publish();
    }

}
