package com.podigua.kafka.visark.home.control;

import atlantafx.base.controls.RingProgressIndicator;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import javafx.scene.control.ProgressIndicator;
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
    /**
     * 文件夹图标
     */
    private final FontIcon folderIcon = new FontIcon(Material2OutlinedAL.FOLDER);
    /**
     * “文件夹打开”图标
     */
    private final FontIcon folderOpenIcon = new FontIcon(Material2OutlinedAL.FOLDER_OPEN);
    /**
     * 节点图标
     */
    private final FontIcon nodeIcon = new FontIcon(AntDesignIconsOutlined.CLUSTER);
    /**
     * 消费者图标
     */
    private final FontIcon consumerIcon = new FontIcon(AntDesignIconsOutlined.USER_SWITCH);
    /**
     * 主题图标
     */
    private final FontIcon topicIcon = new FontIcon(FluentUiRegularMZ.TEXTBOX_20);

    /**
     * 进度条
     */
    private final ProgressIndicator progress;

    public ClusterNodeTreeCell() {
        progress = new ProgressIndicator();
        progress.setPrefSize(12, 12);
        progress.setStyle("-fx-background-color: -color-accent-subtle");
    }


    @Override
    protected void updateItem(ClusterNode node, boolean empty) {
        super.updateItem(node, empty);
        if (empty || node==null) {
            setText(null);
            setGraphic(null);
            return;
        }
        setText(node.label());
        if (node.loading()) {
            setGraphic(progress);
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

    }
}
