package com.podigua.kafka;

import com.podigua.kafka.core.utils.Resources;
import com.podigua.kafka.core.utils.StageUtils;
import com.podigua.kafka.visark.setting.SettingClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class Windows {

    public static void setting(){
        FXMLLoader loader = Resources.getLoader("/fxml/setting.fxml");
        Parent parent = loader.getRoot();
        StageUtils.show(parent, SettingClient.bundle().getString("setting.title"));
    }
}
