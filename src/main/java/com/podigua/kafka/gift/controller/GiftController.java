package com.podigua.kafka.gift.controller;

import com.podigua.kafka.core.utils.Resources;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsFilled;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class GiftController implements Initializable {
    public ImageView wechat;
    public ImageView alipay;
    public FontIcon wecharIcon;
    public FontIcon alipayIcon;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        wechat.setImage(new Image(Resources.getResource("/images/wx.png").toExternalForm()));
        alipay.setImage(new Image(Resources.getResource("/images/zfb.png").toExternalForm()));
        wecharIcon.setIconCode(AntDesignIconsFilled.WECHAT);
        alipayIcon.setIconCode(AntDesignIconsFilled.ALIPAY_CIRCLE);
    }


}
