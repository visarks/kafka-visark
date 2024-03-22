package com.podigua.kafka.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 列表工具类
 *
 * @author podigua
 * @date 2024/03/23
 */
public class Lists {
    /**
     * 创建
     *
     * @param data 数据
     * @return {@link List}<{@link T}>
     */
    public static <T> List<T> of(T... data) {
        List<T> result = new ArrayList<>();
        result.addAll(Arrays.asList(data));
        return result;
    }
}
