package com.podigua.kafka.core.utils;

import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程实用程序
 *
 * @author podigua
 * @date 2024/03/24
 */
public class ThreadUtils {
    private final static ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * 开始
     *
     * @param task 任务
     */
    public static <T> void start(Task<T> task) {
        Thread.ofVirtual().start(task);
    }

    /**
     * 虚拟
     *
     * @return {@link ExecutorService}
     */
    public static ExecutorService virtual() {
        return service;
    }

}
