package com.podigua.kafka.core;

/**
 *
 **/
public interface FilterValue<T> {
    boolean compare(T entity, String filter);
}
