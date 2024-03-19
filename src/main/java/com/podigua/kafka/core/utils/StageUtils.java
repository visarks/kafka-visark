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
        CardHeaderPane header = new CardHeaderPane(stage,title,null);
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

    public static void notice(String title){
//        final var msg = new Notification(
//               title,
//                new FontIcon(Material2OutlinedAL.INFO)
//        );
//        msg.getStyleClass().addAll(
//                Styles.ACCENT, Styles.ELEVATED_1
//        );
//        msg.setPrefHeight(Region.USE_PREF_SIZE);
//        msg.setMaxHeight(Region.USE_PREF_SIZE);
//        StackPane.setAlignment(msg, Pos.TOP_RIGHT);
//        StackPane.setMargin(msg, new Insets(10, 10, 0, 0));
//        var out = Animations.slideOutUp(msg, Duration.millis(250));
//        out.setOnFinished(f -> out.getChildren().remove(msg));
//        out.playFromStart();
    }
}
