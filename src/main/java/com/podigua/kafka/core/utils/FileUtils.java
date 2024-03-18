package com.podigua.kafka.core.utils;

import java.io.*;
import java.util.stream.Collectors;

/**
 * 文件工具类
 *
 * @author podigua
 * @date 2024/03/18
 */
public class FileUtils {
    /**
     * 根路径
     *
     * @return {@link File}
     */
    public static File root() {
        File root = new File(System.getProperty("user.home"), ".kafka-visark");
        if (!root.exists()) {
            if (root.mkdirs()) {
                System.out.println("创建目录成功");
            }
        }
        return root;
    }

    /**
     * 根路径
     *
     * @return {@link File}
     */
    public static File file(String file) {
        return new File(root(), file);
    }

    /**
     * 根路径
     *
     * @return {@link File}
     */
    public static void write(File file, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根路径
     *
     * @return {@link File}
     */
    public static String read(File file) {
        if(file.exists()){
            try (BufferedReader writer = new BufferedReader(new FileReader(file))) {
                return String.join(System.lineSeparator(), writer.lines().collect(Collectors.toList()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
