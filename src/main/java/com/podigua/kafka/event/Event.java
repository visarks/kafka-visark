package com.podigua.kafka.event;

import com.podigua.kafka.core.utils.ThreadUtils;
import com.podigua.kafka.core.utils.UUIDUtils;

/**
 * 事件
 *
 * @author podigua
 * @date 2024/03/21
 */
public class Event {
    /**
     * UUID
     */
    private final String uuid = UUIDUtils.uuid();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event event)) {
            return false;
        }
        return uuid.equals(event.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    /**
     * 发布
     *
     * @param event 事件
     */
    public static <E extends Event> void publish(E event) {
        EventBus.getInstance().publish(event);
    }

    /**
     * 发布
     */
    public void publish() {
        EventBus.getInstance().publish(this);
    }

    /**
     * 发布
     */
    public void publishAsync() {
        ThreadUtils.virtual().execute(() -> {
            EventBus.getInstance().publish(this);
        });
    }
}
