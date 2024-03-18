package com.podigua.kafka.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 **/
public class Lists {
    public static <T> List<T> of(T... data) {
        List<T> result = new ArrayList<>();
        result.addAll(Arrays.asList(data));
        return result;
    }
}
