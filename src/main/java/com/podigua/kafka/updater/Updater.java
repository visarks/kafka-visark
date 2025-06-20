package com.podigua.kafka.updater;

import com.fasterxml.jackson.core.type.TypeReference;
import com.podigua.kafka.State;
import com.podigua.kafka.core.utils.BeanUtils;
import com.podigua.kafka.core.utils.FileUtils;
import com.podigua.kafka.core.utils.MessageUtils;
import com.podigua.kafka.core.utils.StageUtils;
import com.podigua.kafka.updater.ssl.SSLUtils;
import com.podigua.kafka.updater.ssl.TrustAllCerts;
import com.podigua.kafka.updater.ssl.TrustAllHostnameVerifier;
import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.path.utils.SystemUtils;
import javafx.concurrent.Task;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 更新
 *
 * @author podigua
 * @date 2025/03/27
 */
public class Updater {
    private static final Logger logger = LoggerFactory.getLogger(Updater.class);
    public static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .sslSocketFactory(SSLUtils.createSocketFactory(), new TrustAllCerts())
            .hostnameVerifier(new TrustAllHostnameVerifier())
            .build();

    /**
     * 获取版本
     *
     * @return {@link Releases }
     */
    public static Task<Releases> getReleases() {
        return new Task<>() {
            @Override
            protected Releases call() throws Exception {
                try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    logger.info("获取版本,url:{}", com.podigua.kafka.State.URL);
                    Request request = new Request.Builder().url(com.podigua.kafka.State.URL).get().build();
                    Response response = CLIENT.newCall(request).execute();
                    ResponseBody body = response.body();

                    if (body == null) {
                        throw new RuntimeException("获取版本失败");
                    }
                    FileUtils.copy(body.byteStream(), output);
                    String content = output.toString(StandardCharsets.UTF_8);
                    logger.info("获取版本完成:{}", content);
                    Releases result = BeanUtils.readValue(content, new TypeReference<Releases>() {
                    });
                    response.close();
                    if (result != null && StringUtils.hasText(result.getVersion())) {
                        return result;
                    }
                    throw new RuntimeException("获取版本失败1");
                } catch (Exception e) {
                    logger.error("获取版本失败", e);
                    throw new RuntimeException();
                }
            }
        };

    }


    public static void check(boolean tips) {
        Task<Releases> task = getReleases();
        task.setOnSucceeded(handler -> {
            Releases releases = (Releases) handler.getSource().getValue();
            Integer[] latest = version(releases.getVersion());
            Integer[] current = version(State.VERSION);
            if (isNewVersion(current, latest)) {
                Platform platform = getPlatform(releases);
                if (platform == null) {
                    if(tips){
                        MessageUtils.success(SettingClient.bundle().getString("updater.tooltip"));
                    }
                } else {
                    UpdatePane pane = new UpdatePane(releases, platform);
                    Stage stage = StageUtils.none(pane, pane.getOnClose());
                    pane.setStage(stage);
                    stage.initModality(Modality.NONE);
                    stage.show();
                }
            } else {
                if(tips){
                    MessageUtils.success(SettingClient.bundle().getString("updater.tooltip"));
                }
            }
        });
        task.setOnFailed(handler -> {
            Throwable exception = handler.getSource().getException();
            if (exception != null) {
                MessageUtils.error(SettingClient.bundle().getString("updater.error"));
            }
        });
        Thread.ofVirtual().start(task);
    }

    private static Platform getPlatform(Releases releases) {
        String os = "";
        if (SystemUtils.IS_OS_MAC) {
            os = "darwin";
        } else if (SystemUtils.IS_OS_WINDOWS) {
            os = "windows";
        } else if (SystemUtils.IS_OS_LINUX) {
            os = "linux";
        }
        return releases.getPlatforms().get(os + "-" + SystemUtils.OS_ARCH);
    }

    private static boolean isNewVersion(Integer[] source, Integer[] target) {
        int length = Math.min(source.length, target.length);
        for (int index = 0; index < length; index++) {
            if (target[index] > source[index]) {
                return true;
            }
        }
        return false;
    }

    public static Integer[] version(String version) {
        String[] array = version.split("[.]]");
        Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = parseVersion(array[i]);
        }
        return result;
    }

    private static Integer parseVersion(String value) {
        StringBuilder result = new StringBuilder();
        char[] array = value.toCharArray();
        for (char c : array) {
            if (c >= '0' && c <= '9') {
                result.append(c);
            }
        }
        return Integer.parseInt(result.toString());
    }
}
