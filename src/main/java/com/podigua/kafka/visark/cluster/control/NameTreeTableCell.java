package com.podigua.kafka.visark.cluster.control;

import com.podigua.kafka.visark.cluster.entity.ClusterProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;
import org.kordamp.ikonli.antdesignicons.AntDesignIconsOutlined;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.springframework.util.StringUtils;

/**
 *
 **/
public class NameTreeTableCell extends TreeTableCell<ClusterProperty,ClusterProperty> {
    /**
     * 文件夹图标
     */
    private final FontIcon folderIcon = new FontIcon(Material2OutlinedAL.FOLDER);
    /**
     * 文件夹图标
     */
    private final FontIcon folderOpenIcon = new FontIcon(Material2OutlinedAL.FOLDER_OPEN);
    /**
     * 节点图标
     */
    private final FontIcon nodeIcon = new FontIcon(AntDesignIconsOutlined.CLUSTER);

    public NameTreeTableCell() {
    }

    @Override
    protected void updateItem(ClusterProperty node, boolean empty) {
        if (empty) {
            setText(null);
            setGraphic(null);
            return;
        }
        TreeTableRow<ClusterProperty> item = getTableRow();
        if(item==null || item.getTreeItem()==null || item.getTreeItem().getValue()==null){
            return;
        }
        TreeItem<ClusterProperty> treeItem = item.getTreeItem();
        ClusterProperty value = item.getItem();
        if(!StringUtils.hasText(value.getName())){

        }
        setText(value.getName());
        switch (value.getType()) {
            case "folder":
                if(treeItem.isExpanded()){
                    setGraphic(folderOpenIcon);
                }else{
                    setGraphic(folderIcon);
                }
                break;
            case "cluster":
                setGraphic(this.nodeIcon);
                break;
        }

    }

}
