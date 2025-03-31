package com.podigua.kafka.updater;

import com.fasterxml.jackson.core.type.TypeReference;
import com.podigua.kafka.State;
import com.podigua.kafka.core.utils.BeanUtils;
import com.podigua.kafka.core.utils.FileUtils;
import com.podigua.kafka.core.utils.MessageUtils;
import com.podigua.kafka.core.utils.StageUtils;
import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.path.utils.SystemUtils;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private static final String URL = "http://localhost:8080/releases/kafka-visark";
    public static final OkHttpClient CLIENT = new OkHttpClient();

    public static void main(String[] args) throws IOException {
        String url = "http://localhost:8080/releases/kafka-visark/darwin-x86_64";
        Request request = new Request.Builder().url(url).get().build();
        Response response = CLIENT.newCall(request).execute();
        ResponseBody body = response.body();
        System.out.println("");
    }

    /**
     * 获取版本
     *
     * @return {@link Releases }
     */
    public static Releases getReleases() {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Request request = new Request.Builder().url(URL).get().build();
            Response response = CLIENT.newCall(request).execute();
            ResponseBody body = response.body();
            InputStream stream = body.byteStream();
            IOUtils.copy(stream, output);
            String content = new String(output.toByteArray(), StandardCharsets.UTF_8);
            logger.info("获取版本:{}", content);
            IOUtils.closeQuietly(stream);
            Result<Releases> result = BeanUtils.readValue(content, new TypeReference<Result<Releases>>() {
            });
            if (result != null && Boolean.TRUE.equals(result.getSuccess())) {
                return result.getData();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }


    public static void check() {
        try {
            Releases releases = getReleases();
            Integer[] latest = version(releases.getVersion());
            Integer[] current = version(State.VERSION);
            if (isNewVersion(current, latest)) {
                Platform platform = getPlatform(releases);
                if (platform == null) {
                    MessageUtils.warning(SettingClient.bundle().getString("updater.platform.error"));
                } else {
                    UpdatePane pane = new UpdatePane(releases, platform);
                    Stage stage = StageUtils.none(pane);
                    pane.setStage(stage);
                    stage.show();
                }
            } else {
                MessageUtils.success(SettingClient.bundle().getString("updater.tooltip"));
            }
        } catch (Exception e) {
            MessageUtils.error(SettingClient.bundle().getString("updater.error"));
            logger.error("获取版本失败", e);
        }
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
