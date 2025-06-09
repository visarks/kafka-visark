package com.podigua.kafka.visark.settings;

import com.podigua.kafka.core.utils.DatasourceUtils;
import com.podigua.kafka.core.utils.UUIDUtils;
import com.podigua.kafka.visark.settings.entity.TopicSettingProperty;
import org.springframework.util.StringUtils;

public class TopicSettingClient {
    private static final String INSERT = "insert into topic_setting (id,clusterId, topic,keyType,keyCharset,valueType,valueCharset,keyProtobufFile,valueProtobufFile) values ('%s','%s', '%s','%s','%s','%s','%s','%s','%s')";
    private static final String UPDATE = "update topic_setting set keyType = '%s',keyCharset='%s',valueType='%s',valueCharset='%s',keyProtobufFile='%s',valueProtobufFile='%s' where id = '%s'";
    private static final String SELECT = "select * from topic_setting where clusterId='%s' and topic='%s'";

    private static final String DELETE = "delete from topic_setting where clusterId = '%s',and topic='%s'";


    public static void save(TopicSettingProperty setting) {
        if (StringUtils.hasText(setting.getId())) {
            String sql = String.format(UPDATE, setting.getKeyType(), setting.getKeyCharset(), setting.getValueType(), setting.getValueCharset(),
                    setting.getKeyProtobufFile(),
                    setting.getValueProtobufFile(),
                    setting.getId());
            DatasourceUtils.execute(sql);
        } else {
            setting.setId(UUIDUtils.uuid());
            String sql = String.format(INSERT, setting.getId(), setting.getClusterId(), setting.getTopic(), setting.getKeyType(), setting.getKeyCharset(), setting.getValueType(), setting.getValueCharset(),setting.getKeyProtobufFile(),setting.getValueProtobufFile());
            DatasourceUtils.execute(sql);
        }
    }

    public static TopicSettingProperty getByClusterAndTopic(String clusterId, String topic) {
        String sql = String.format(SELECT, clusterId, topic);
        TopicSettingProperty result = DatasourceUtils.query4Object(sql, TopicSettingProperty.class);
        if (result == null) {
            return TopicSettingProperty.create(clusterId, topic);
        }
        return result;
    }

    public static void deleteByClusterAndTopic(String clusterId, String topic) {
        String sql = String.format(DELETE, clusterId, topic);
        DatasourceUtils.execute(sql);
    }
}
