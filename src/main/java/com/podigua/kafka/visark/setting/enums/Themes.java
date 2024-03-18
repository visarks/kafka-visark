package com.podigua.kafka.visark.setting.enums;

import atlantafx.base.theme.*;

/**
 * 主题
 *
 * @author podigua
 * @date 2024/03/18
 */
public enum Themes {
    /**
     * PrimerLight
     */
    primer_light(new PrimerLight()),
    /**
     * PrimerDark
     */
    primer_dark(new PrimerDark()),
    /**
     * NordLight
     */
    nord_light(new NordLight()),
    /**
     * NordDark
     */
    nord_dark(new NordDark()),
    /**
     * CupertinoLight
     */
    cupertino_light(new CupertinoLight()),
    /**
     * CupertinoDark
     */
    cupertino_dark(new CupertinoDark()),
    /**
     * Dracula
     */
    dracula(new Dracula());

    private final Theme theme;

    Themes(Theme theme) {
        this.theme = theme;
    }

    @Override
    public String toString() {
        return theme.getName();
    }

    public Theme theme() {
        return this.theme;
    }
}
