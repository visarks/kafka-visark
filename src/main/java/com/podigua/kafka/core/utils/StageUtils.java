package com.podigua.kafka.core.utils;

import atlantafx.base.controls.Card;
import com.podigua.kafka.State;
import com.podigua.kafka.core.CardHeaderPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 *
 **/
public class StageUtils {
    /**
     * 显示
     *
     * @param parent 父母
     * @param title  标题
     * @return {@link Stage}
     */
    public static Stage show(Parent parent, String title, Window window) {
        Stage stage = new Stage();
        Card card = new Card();
        CardHeaderPane header = new CardHeaderPane(stage,title,null);
        card.setHeader(header);
        card.setBody(parent);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(card);
        scene.getStylesheets().add(Resources.getResource("/css/main.css").toExternalForm());
        stage.setScene(scene);
        stage.initOwner(window);
        stage.setTitle(title);
        stage.show();
        return stage;
    }

    /**
     *
     *
     * @param body   身体
     * @param window 窗
     * @return {@link Stage}
     */
    public static Stage body(Parent body,Window window) {
        Stage stage = new Stage();
        Card card = new Card();
        card.setBody(body);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(card);
        scene.getStylesheets().add(Resources.getResource("/css/main.css").toExternalForm());
        stage.setScene(scene);
        stage.initOwner(window);
        stage.show();
        return stage;
    }
    /**
     * 显示
     *
     * @param parent 父母
     * @param title  标题
     * @return {@link Stage}
     */
    public static Stage show(Parent parent, String title) {
        return show(parent,title,State.stage());
    }

}
