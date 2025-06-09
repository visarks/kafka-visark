package com.podigua.kafka.visark.settings.entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 设置属性
 *
 * @author podigua
 * @date 2024/03/18
 */
public class TopicSettingProperty {
    private String id;
    private String clusterId;
    private String topic;
    private SimpleStringProperty keyType = new SimpleStringProperty("String");
    private final SimpleStringProperty keyCharset = new SimpleStringProperty("UTF-8");
    private final SimpleStringProperty valueType = new SimpleStringProperty("String");
    private final SimpleStringProperty valueCharset = new SimpleStringProperty("UTF-8");
    private final SimpleStringProperty keyProtobufFile = new SimpleStringProperty("");
    private final SimpleStringProperty valueProtobufFile = new SimpleStringProperty("");

    public static TopicSettingProperty create(String clusterId, String topic) {
        TopicSettingProperty result=new TopicSettingProperty(clusterId,topic);
        return result;
    }
    public TopicSettingProperty(){

    }
    public TopicSettingProperty(String clusterId, String topic) {
        this.clusterId = clusterId;
        this.topic = topic;
    }

    public void setKeyType(String keyType) {
        this.keyType.set(keyType);
    }

    public String getKeyType() {
        return keyType.get();
    }

    public SimpleStringProperty keyTypeProperty() {
        return keyType;
    }
    public void setKeyCharset(String keyCharset) {
        this.keyCharset.set(keyCharset);
    }
    public String getKeyCharset() {
        return keyCharset.get();
    }

    public SimpleStringProperty keyCharsetProperty() {
        return keyCharset;
    }

    public void setValueType(String valueType) {
        this.valueType.set(valueType);
    }
    public String getValueType() {
        return valueType.get();
    }

    public SimpleStringProperty valueTypeProperty() {
        return valueType;
    }

    public void setValueCharset(String valueCharset) {
        this.valueCharset.set(valueCharset);
    }
    public String getValueCharset() {
        return valueCharset.get();
    }

    public SimpleStringProperty valueCharsetProperty() {
        return valueCharset;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getTopic() {
        return topic;
    }

    public void setKeyProtobufFile(String keyProtobufFile) {
        this.keyProtobufFile.set(keyProtobufFile);
    }
    public String getKeyProtobufFile() {
        return keyProtobufFile.get();
    }

    public SimpleStringProperty keyProtobufFileProperty() {
        return keyProtobufFile;
    }
    public void setValueProtobufFile(String valueProtobufFile){
        this.valueProtobufFile.set(valueProtobufFile);
    }

    public String getValueProtobufFile() {
        return valueProtobufFile.get();
    }

    public SimpleStringProperty valueProtobufFileProperty() {
        return valueProtobufFile;
    }
}
