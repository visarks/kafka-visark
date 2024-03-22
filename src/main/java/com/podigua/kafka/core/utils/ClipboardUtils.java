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
    public static void copy(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }
}
