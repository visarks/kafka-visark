package com.podigua.kafka.core.utils;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;


/**
 * 剪贴板工具类
 *
 * @author podigua
 * @date 2024/03/22
 */
public class ClipboardUtils {
    /**
     * 复制
     *
     * @param text 发短信
     * @return boolean
     */
    public static boolean copy(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        return clipboard.setContent(content);
    }
}
