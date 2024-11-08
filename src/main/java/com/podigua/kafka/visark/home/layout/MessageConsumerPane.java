package com.podigua.kafka.visark.home.layout;

import com.podigua.kafka.core.utils.Messages;
import com.podigua.kafka.visark.home.entity.ClusterNode;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * 消息使用者窗格
 *
 * @author podigua
 * @date 2024/03/25
 */
public class MessageConsumerPane extends ContentBorderPane {
    private final ClusterNode value;

    public MessageConsumerPane(ClusterNode value) {
        ShowConsumerDetailPane detail = new ShowConsumerDetailPane(value.clusterId(), value.label());
        ShowConsumerMemberPane member = new ShowConsumerMemberPane(value.clusterId(), value.label());
        ShowConsumerOffsetPane offset = new ShowConsumerOffsetPane(value.clusterId(), value.label());
        this.value = value;
//        this.setTop(detail);
        TabPane tab = new TabPane();
        tab.getTabs().addAll(tab(Messages.members(), member), tab(Messages.offset(), offset));
        this.setCenter(tab);
    }

    private Tab tab(String title, Node node) {
        Tab result = new Tab(title);
        result.setContent(node);
        result.setClosable(false);
        return result;
    }

    /**
     * @return {@link ClusterNode}
     */
    public ClusterNode value() {
        return value;
    }

    @Override
    public void close() {

    }
}
