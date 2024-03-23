package com.podigua.kafka.admin.executor;

/**
 * 失败回调
 *
 * @author podigua
 * @date 2024/03/24
 */
public interface FailCallback {
    void handler(Throwable throwable);
}
