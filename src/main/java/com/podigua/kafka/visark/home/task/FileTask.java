package com.podigua.kafka.visark.home.task;

import com.podigua.kafka.visark.home.entity.Message;
import javafx.concurrent.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Excel 输出任务
 *
 * @author podigua
 * @date 2024/12/06
 */
public class FileTask extends Task<Boolean> {
    private final File target;
    private final BufferedWriter writer;
    private boolean running = true;
    private final List<Message> messages;

    public FileTask(File target, List<Message> messages) {
        this.target = target;
        try {
            writer = new BufferedWriter(new FileWriter(target));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.messages = messages;
    }

    public void shutdown() {
        this.running = false;
    }

    @Override
    protected Boolean call() throws Exception {
        for (Message message : this.messages) {
            if (!running) {
                break;
            }
            writer.write(new String(message.value().getValue()));
            writer.newLine();
        }
        return running;
    }

    @Override
    protected void cancelled() {
        this.close();
    }

    private void close() {
        try {
            this.writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void succeeded() {
        this.close();
    }

    @Override
    protected void failed() {
        this.close();
    }
}
