package com.podigua.kafka.event;

/**
 * 提示
 *
 * @author podigua
 * @date 2024/03/26
 */
public class TooltipEvent extends Event {
    private final TooltipType type;

    private final String tooltip;

    public TooltipEvent(TooltipType type, String tooltip) {
        this.type = type;
        this.tooltip = tooltip;
    }

    /**
     * 信息
     *
     * @param message 消息
     * @return {@link TooltipEvent}
     */
    public static TooltipEvent info(String message) {
        return new TooltipEvent(TooltipType.info, message);
    }

    public TooltipType type() {
        return type;
    }

    /**
     * 工具提示
     *
     * @return {@link String}
     */
    public String tooltip() {
        return tooltip;
    }

    public enum TooltipType {
        info
    }
}
