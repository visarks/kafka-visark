package com.podigua.kafka.updater;

import com.podigua.path.Paths;
import javafx.concurrent.Task;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URLDecoder;

public class UpdateTask extends Task<File> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateTask.class);
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String FILENAME = "filename=";
    private final Platform platform;

    public UpdateTask(Platform platform) {
        this.platform = platform;
    }

    @Override
    protected File call() throws Exception {
        Request request = new Request.Builder().url(platform.getUrl()).get().build();
        Response response = Updater.CLIENT.newCall(request).execute();
        logger.info("下载文件,地址:{}", platform.getUrl());
        String disposition = response.header(CONTENT_DISPOSITION);
        if (!StringUtils.hasText(disposition)) {
            updateMessage("更新失败");
            throw new RuntimeException("更新失败");
        }
        int index = disposition.indexOf(FILENAME);
        if (index < 0) {
            updateMessage("更新失败");
            throw new RuntimeException("更新失败");
        }
        String filename = disposition.substring(index + FILENAME.length());
        filename= URLDecoder.decode(filename, "UTF-8");
        ResponseBody body = response.body();
        long total = body.contentLength();
        String size=size(total);
        logger.info("下载文件,文件名称:{},大小:{}",filename,total);
        File target = new File(Paths.downloads(), filename);
        try (InputStream inputStream = body.byteStream();
             BufferedInputStream bis = new BufferedInputStream(inputStream);
             FileOutputStream fos = new FileOutputStream(target)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            long loaded = 0;
            while ((bytesRead = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                loaded += bytesRead;
                double progress = (double) loaded / total;
                updateProgress(loaded, total);
                updateMessage(progress(progress)+" ("+size(loaded)+"/"+size+")");
            }
        } catch (IOException e) {
            updateMessage("下载失败：" + e.getMessage());
            throw e;
        }
        return target;
    }

    public static String progress(double progress){
        return String.format("%.2f%%", progress * 100);
    }

    public static void main(String[] args) {
        System.out.println(size(100L));
    }
    private static String size(long size) {
        if (size < 1024) {
            return String.format("%.2fB", (double) size);
        } else if (size < 1024 * 1024) {
            return String.format("%.2fKB", (double) size / 1024);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2fMB", (double) size / (1024 * 1024));
        } else {
            return String.format("%.2fGB", (double) size / (1024 * 1024 * 1024));
        }
    }
}
