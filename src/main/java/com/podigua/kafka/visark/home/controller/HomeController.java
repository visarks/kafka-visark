package com.podigua.kafka.visark.home.controller;

import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.core.utils.StageUtils;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 主页 控制器
 *
 * @author podigua
 * @date 2024/03/18
 */
public class HomeController implements Initializable {
    public ComboBox<String> clusterSelect;
    public Button clusterButton;
    public Button settingButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clusterButton.setGraphic(new FontIcon(Material2OutlinedAL.FOLDER));
        settingButton.setGraphic(new FontIcon(Material2OutlinedMZ.SETTINGS));
    }

    public void showSetting(ActionEvent event) {
        FXMLLoader loader = Resources.getLoader("/fxml/setting.fxml");
        Parent parent = loader.getRoot();
        StageUtils.show(parent, SettingClient.bundle().getString("setting.title"));

    }

    public void showCluster(ActionEvent event) {
        FXMLLoader loader = Resources.getLoader("/fxml/cluster.fxml");
        Parent parent = loader.getRoot();
        StageUtils.show(parent, SettingClient.bundle().getString("cluster.title"));
    }
}
