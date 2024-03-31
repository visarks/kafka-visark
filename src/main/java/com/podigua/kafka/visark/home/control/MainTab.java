package com.podigua.kafka.visark.home.control;

import com.podigua.kafka.visark.home.entity.ClusterNode;
import com.podigua.kafka.visark.home.enums.NodeType;
import com.podigua.kafka.visark.home.layout.MessageConsumerPane;
import com.podigua.kafka.visark.home.layout.TopicMessagePane;
import javafx.scene.control.Tab;

/**
 * 主选项卡
 *
 * @author podigua
 * @date 2024/03/28
 */
public class MainTab extends Tab {
    private final ClusterNode value;

    public MainTab(ClusterNode value) {
        this.value = value;
        this.setId(value.id());
        this.setText(value.label());
        if(NodeType.topic.equals(this.value.type())){
            this.setContent(new TopicMessagePane(this.value));
        }else{
            this.setContent(new MessageConsumerPane(this.value));
        }
    }
}
