package com.podigua.kafka.admin.task;

import com.podigua.kafka.admin.QueryTask;
import com.podigua.kafka.visark.settings.TopicSettingClient;
import com.podigua.kafka.visark.settings.entity.TopicSettingProperty;

/**
 * 保存topic 设置
 *
 * @author podigua
 * @date 2024/03/23
 */
public class TopicSettingTask extends QueryTask<Void> {
    /**
     * 主题
     */
    private final TopicSettingProperty property;

    public TopicSettingTask(TopicSettingProperty property) {
        super(property.getClusterId());
        this.property = property;
    }

    @Override
    protected Void call() throws Exception {
        TopicSettingClient.save(property);
        return null;
    }
}
