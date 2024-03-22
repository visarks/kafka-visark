package com.podigua.kafka.core.event;

import atlantafx.base.controls.Notification;
import com.podigua.kafka.event.Event;
import javafx.util.Duration;

/**
 * 通知事件
 *
 * @author podigua
 * @date 2024/03/22
 */
public class NoticeEvent extends Event {
    private final Notification notification;

    private Duration duration = Duration.seconds(3);

    public NoticeEvent(Notification notification) {
        this.notification = notification;
    }

    /**
     * 消息持续时间
     *
     * @param duration 期间
     * @return {@link NoticeEvent}
     */
    public NoticeEvent duration(Duration duration) {
        if (Duration.INDEFINITE == duration || Duration.UNKNOWN == duration) {
            throw new RuntimeException("not support");
        }
        this.duration = duration;
        return this;
    }

    public Duration duration() {
        return duration;
    }

    public Notification notification() {
        return notification;
    }
}
