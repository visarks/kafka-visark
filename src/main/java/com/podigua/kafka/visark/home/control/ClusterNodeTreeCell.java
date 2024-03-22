package com.podigua.kafka.visark.home.control;

import com.podigua.kafka.visark.home.entity.ClusterNode;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

/**
 * 群集节点树单元
 *
 * @author podigua
 * @date 2024/03/22
 */
public class ClusterNodeTreeCell extends TreeCell<ClusterNode> {
    private final FontIcon folderIcon = new FontIcon(Material2OutlinedAL.FOLDER);
    private final FontIcon folderOpenIcon = new FontIcon(Material2OutlinedAL.FOLDER_OPEN);
    private final FontIcon nodeIcon = new FontIcon(AntDesignIconsOutlined.CLUSTER);
    private final FontIcon consumerIcon = new FontIcon(AntDesignIconsOutlined.USER_SWITCH);
    private final FontIcon topicIcon = new FontIcon(FluentUiRegularMZ.TEXTBOX_20);


    @Override
    protected void updateItem(ClusterNode node, boolean empty) {
        super.updateItem(node, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
            return;
        }
        switch (node.type()) {
            case cluster:
            case nodes:
            case topics:
            case consumers:
                TreeItem<ClusterNode> item = getTreeItem();
                if (item.isExpanded()) {
                    setGraphic(folderOpenIcon);
                } else {
                    setGraphic(folderIcon);
                }
                break;
            case node:
                setGraphic(this.nodeIcon);
                break;
            case consumer:
                setGraphic(consumerIcon);
                break;
            case topic:
                setGraphic(topicIcon);
                break;
        }
        setText(node.label());
    }
}
