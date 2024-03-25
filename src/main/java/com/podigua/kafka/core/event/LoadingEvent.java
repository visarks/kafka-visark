package com.podigua.kafka.core.event;

import com.podigua.kafka.event.Event;

/**
 * 加载事件
 *
 * @author podigua
 * @date 2024/03/25
 */
public class LoadingEvent extends Event {
    public static LoadingEvent LOADING=new LoadingEvent(true);
    public static LoadingEvent STOP=new LoadingEvent(false);
    private final boolean loading;

    public LoadingEvent(Boolean loading) {
        this.loading = loading;
    }

    public boolean loading() {
        return loading;
    }
}
