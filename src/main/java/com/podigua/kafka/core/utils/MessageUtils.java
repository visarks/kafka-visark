package com.podigua.kafka.core.utils;

import atlantafx.base.controls.Message;
import atlantafx.base.theme.Styles;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

/**
 *
 **/
public class MessageUtils {

    public static  void show(String title,String description){
        var message = new Message(
                "Quote",
                description,
                new FontIcon(Material2OutlinedAL.CHECK_CIRCLE_OUTLINE)
        );
        message.getStyleClass().add(Styles.SUCCESS);
    }
}
