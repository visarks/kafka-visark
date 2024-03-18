package com.podigua.kafka.visark.setting.controller;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.ResourceBundleService;
import com.dlsc.formsfx.view.controls.SimpleComboBoxControl;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.kafka.visark.setting.entity.SettingProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.spi.ResourceBundleProvider;

/**
 * 设置控制器
 *
 * @author podigua
 * @date 2024/03/18
 */
public class SettingController implements Initializable {
    private final SettingProperty settingProperty = SettingClient.get();
    public AnchorPane center;
    private Form form;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ResourceBundleService service=new ResourceBundleService(SettingClient.bundle());
        if (form == null) {
            form = Form.of(Group.of(
                    Field.ofSingleSelectionType(new SimpleListProperty<>(SettingClient.LANGUAGES), settingProperty.language())
                            .label("setting.form.language").render(new SimpleComboBoxControl<>(){

                            }),
                    Field.ofSingleSelectionType(new SimpleListProperty<>(SettingClient.THEMES), settingProperty.theme())
                            .label("setting.form.theme")

            )).title("setting.title").i18n(service);
        }
        FormRenderer renderer = new FormRenderer(form);
        center.getChildren().add(renderer);
        AnchorPane.setTopAnchor(renderer,0.0);
        AnchorPane.setRightAnchor(renderer,0.0);
        AnchorPane.setBottomAnchor(renderer,0.0);
        AnchorPane.setLeftAnchor(renderer,0.0);
    }
}
