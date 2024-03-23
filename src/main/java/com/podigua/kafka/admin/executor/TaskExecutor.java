package com.podigua.kafka.admin.executor;

import com.podigua.kafka.core.utils.ThreadUtils;
import javafx.concurrent.Task;

/**
 * 任务执行程序
 *
 * @author podigua
 * @date 2024/03/24
 */
public abstract class TaskExecutor<T> {
    /**
     * 任务
     */
    private final Task<T> task;

    /**
     * 成功
     */
    private SuccessCallback<T> success;
    /**
     * 失败
     */
    private FailCallback fail;

    public TaskExecutor(Task<T> task) {
        this.task = task;
        this.task.setOnSucceeded(event -> {
            if (this.success != null) {
                this.success.handler(task.getValue());
            }
        });
        this.task.setOnFailed(event -> {
            if (this.fail != null) {
                this.fail.handler(event.getSource().getException());
            }
        });
    }


    /**
     * 成功回调
     *
     * @param success 成功回调
     * @return {@link TopicExistExecutor}
     */
    public <O extends TaskExecutor<T>> O success(SuccessCallback<T> success) {
        this.success = success;
        return (O) this;
    }

    /**
     * 失败回调
     *
     * @param fail 失败回调
     * @return {@link TopicExistExecutor}
     */
    public <O extends TaskExecutor<T>> O fail(FailCallback fail) {
        this.fail = fail;
        return (O) this;
    }

    /**
     * 执行
     */
    public void execute() {
        ThreadUtils.start(task);
    }
}
