package com.podigua.kafka.core.utils;

import atlantafx.base.controls.Card;
import com.podigua.kafka.State;
import com.podigua.kafka.core.CardHeaderPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    public static Stage show(Parent parent, String title) {
        Stage stage = new Stage();
        Card card = new Card();
        CardHeaderPane header = new CardHeaderPane(stage,title);
        card.setHeader(header);
        card.setBody(parent);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(card);
        scene.getStylesheets().add(Resources.getResource("/css/main.css").toExternalForm());
        stage.setScene(scene);
        stage.initOwner(State.stage());
        stage.setTitle(title);
        stage.show();
        return stage;
    }
}
