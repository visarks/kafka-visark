package com.podigua.kafka.core.utils;

import atlantafx.base.controls.Card;
import com.podigua.kafka.State;
import com.podigua.kafka.core.CardHeaderPane;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    public static Stage show(Parent parent, String title, Modality modality) {
        Stage stage = new Stage();
        Card card = new Card();
        CardHeaderPane header = new CardHeaderPane(stage,title,null);
        card.setHeader(header);
        card.setBody(parent);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(modality);
        Scene scene = new Scene(card);
        scene.getStylesheets().add(Resources.getResource("/css/main.css").toExternalForm());
        stage.setScene(scene);
        stage.initOwner(State.stage());
        stage.show();
        stage.addEventHandler(KeyEvent.KEY_PRESSED,event->{
            if(KeyCode.ESCAPE==event.getCode()){
                stage.close();
            }
        });
        parent.requestFocus();
        return stage;
    }
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
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(card);
        scene.getStylesheets().add(Resources.getResource("/css/main.css").toExternalForm());
        stage.setScene(scene);
        stage.initOwner(window);
        stage.show();
        stage.addEventHandler(KeyEvent.KEY_PRESSED,event->{
            if(KeyCode.ESCAPE==event.getCode()){
                stage.close();
            }
        });
        parent.requestFocus();
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

    public static Stage none(Node parent) {
        return none(parent,null);
    }
    public static Stage none(Node parent,Runnable runnable) {
        Stage stage = new Stage();
        Card card = new Card();
        CardHeaderPane header = new CardHeaderPane(stage,null,runnable);
        card.setHeader(header);
        card.setBody(parent);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(card);
        scene.getStylesheets().add(Resources.getResource("/css/main.css").toExternalForm());
        stage.setScene(scene);
        stage.initOwner(State.stage());
        if(runnable==null){
            stage.addEventHandler(KeyEvent.KEY_PRESSED,event->{
                if(KeyCode.ESCAPE==event.getCode()){
                    stage.close();
                }
            });
        }
        return stage;
    }
}
