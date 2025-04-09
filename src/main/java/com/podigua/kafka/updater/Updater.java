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
            .sslSocketFactory(SSLUtils.createSocketFactory(),new TrustAllCerts())
            .hostnameVerifier(new TrustAllHostnameVerifier())
            .build();

    /**
     * 获取版本
     *
     * @return {@link Releases }
     */
    public static Releases getReleases() {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            logger.info("获取版本,url:{}",State.URL);
            Request request = new Request.Builder().url(State.URL).get().build();
            Response response = CLIENT.newCall(request).execute();
            ResponseBody body = response.body();
            InputStream stream = body.byteStream();
            FileUtils.copy(stream, output);
            String content = new String(output.toByteArray(), StandardCharsets.UTF_8);
            logger.info("获取版本完成:{}", content);
            Releases result = BeanUtils.readValue(content, new TypeReference<Releases>() {
            });
            if (result != null && StringUtils.hasText(result.getVersion())) {
                return result;
            }
            return null;
        } catch (Exception e) {
            logger.error("获取版本失败",e);
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
                    MessageUtils.success(SettingClient.bundle().getString("updater.tooltip"));
                } else {
                    UpdatePane pane = new UpdatePane(releases, platform);
                    Stage stage = StageUtils.none(pane,pane.getOnClose());
                    pane.setStage(stage);
                    stage.initModality(Modality.NONE);
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
