package com.podigua.kafka.visark.setting.enums;

import java.util.Locale;

/**
 * 语言
 *
 * @author podigua
 * @date 2024/03/18
 */
public enum Language {
    /**
     * 简体中文
     */
    zh_cn("简体中文", Locale.SIMPLIFIED_CHINESE),
    /**
     * 繁體中文
     */
    zh_taiwan("繁體中文", Locale.TRADITIONAL_CHINESE),
    /**
     * English
     */
    english("English", Locale.ENGLISH);

    private final String name;
    private final Locale locale;

    Language(String name, Locale locale) {
        this.name = name;
        this.locale = locale;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Locale locale() {
        return this.locale;
    }
}
