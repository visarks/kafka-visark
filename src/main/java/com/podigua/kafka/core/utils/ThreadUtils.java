package com.podigua.kafka.core.utils;

import javafx.concurrent.Task;

/**
 * 线程实用程序
 *
 * @author podigua
 * @date 2024/03/24
 */
public class ThreadUtils {
    /**
     * 开始
     *
     * @param task 任务
     */
    public static <T> void start(Task<T> task) {
        new Thread(task).start();
    }
}
