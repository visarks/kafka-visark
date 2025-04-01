package com.podigua.kafka.core.utils;

import com.podigua.path.Paths;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

/**
 * 文件工具类
 *
 * @author podigua
 * @date 2024/03/18
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final String MD5 = "MD5";
    private static final String SHA_1 = "SHA-1";
    private static final String SHA_256 = "SHA-256";
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * 根路径
     *
     * @return {@link File}
     */
    public static File root() {
        File root = new File(Paths.appData());
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
        File result = new File(root(), file);
        if (!result.getParentFile().exists()) {
            result.getParentFile().mkdirs();
        }
        return result;
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
        if (file.exists()) {
            try (BufferedReader writer = new BufferedReader(new FileReader(file))) {
                return writer.lines().collect(Collectors.joining(System.lineSeparator()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static File guess(File folder, String filename) {
        File target = new File(folder, filename);
        if (!target.exists()) {
            return target;
        }
        String name = FilenameUtils.getBaseName(filename);
        int index = 1;
        while (true){
            File result = new File(folder, name+"-"+(index++)+"."+FilenameUtils.getExtension(filename));
            if (!result.exists()) {
                return result;
            }
        }

    }

    /**
     * 计算文件的 SHA-256 哈希值
     *
     * @param file 文件
     * @throws IOException              如果读取文件失败
     * @throws NoSuchAlgorithmException 如果算法不可用
     */
    public static String sha256(File file) throws Exception {
        return hex(file, MessageDigest.getInstance(SHA_256));
    }

    /**
     * SHA1
     *
     * @param file 文件
     * @return {@link String }
     * @throws Exception 例外
     */
    public static String sha1(File file) throws Exception {
        return hex(file, MessageDigest.getInstance(SHA_1));
    }

    /**
     * md5
     *
     * @param file 文件
     * @return {@link String }
     * @throws Exception 例外
     */
    public static String md5(File file) throws Exception {
        return hex(file, MessageDigest.getInstance(MD5));
    }

    /**
     * 对文件进行哈希
     *
     * @param file   文件
     * @param digest 消化
     * @return {@link String }
     * @throws Exception 例外
     */
    private static String hex(File file, MessageDigest digest) throws Exception {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        byte[] bytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    public static void copy(InputStream input, FileOutputStream output) {
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
        } catch (Exception e) {
            logger.error("复制失败", e);
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                logger.error("关闭", e);
            }
        }
    }
}
