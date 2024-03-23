package com.podigua.kafka.admin.executor;

/**
 * 执行器回调
 *
 * @author podigua
 * @date 2024/03/24
 */
public interface SuccessCallback<T> {
    /**
     * 成功时
     *
     * @param t t
     */
    void handler(T t);
}
