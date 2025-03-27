package com.podigua.kafka.updater;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 释放
 *
 * @author podigua
 * @date 2025/03/27
 */
public class Releases {
    /**
     * 版本
     */
    private String version;

    /**
     * 笔记
     */
    private String notes;

    /**
     * 发布日期
     */
    private String publishDate;

    /**
     * 平台
     */
    private Map<String,Platform> platforms=new LinkedHashMap<>();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public Map<String, Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Map<String, Platform> platforms) {
        this.platforms = platforms;
    }
}
