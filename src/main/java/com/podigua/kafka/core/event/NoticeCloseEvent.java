package com.podigua.kafka.core.event;

import atlantafx.base.controls.Notification;
import com.podigua.kafka.event.Event;

import java.time.Duration;

/**
 * 通知关闭事件
 *
 * @author podigua
 * @date 2024/03/22
 */
public class NoticeCloseEvent extends Event {
    private final Notification notification;

    public NoticeCloseEvent(Notification notification) {
        this.notification = notification;
    }

    public Notification notification() {
        return notification;
    }
}
