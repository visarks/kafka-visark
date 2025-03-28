package com.podigua.kafka.updater;

import com.fasterxml.jackson.core.type.TypeReference;
import com.podigua.kafka.State;
import com.podigua.kafka.core.utils.BeanUtils;
import com.podigua.kafka.core.utils.FileUtils;
import com.podigua.kafka.core.utils.MessageUtils;
import com.podigua.kafka.core.utils.StageUtils;
import com.podigua.kafka.visark.setting.SettingClient;
import com.podigua.path.utils.SystemUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * 更新
 *
 * @author podigua
 * @date 2025/03/27
 */
public class Updater {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final String URL = "http://localhost:8080/releases";

    /**
     * 获取版本
     *
     * @return {@link Releases }
     */
    public static Releases getReleases() {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            URL url = new URL(URL);
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            IOUtils.copy(stream, output);
            String content = new String(output.toByteArray(), StandardCharsets.UTF_8);
            IOUtils.closeQuietly(stream);
            return BeanUtils.readValue(content, new TypeReference<Releases>() {
            });
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        check();
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
                    StageUtils.none(new UpdatePane(releases)).show();
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

    public static void download(Releases releases) {

    }
}
