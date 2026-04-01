package com.podigua.kafka.core.utils;


import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TableView;

import java.util.Comparator;
import java.util.function.Function;

/**
 *
 **/
public class TableUtils {
    public static   <T> void sortPolicyProperty(TableView<T> table, FilteredList<T> filters,Function<T, String> function){
        table.sortPolicyProperty().set(_ -> {
             final Comparator<T> tableComparator = table.getComparator();
            Comparator<T> comparator = tableComparator == null ? null : (o1, o2) -> {
                final int tagCompare = function.apply(o1).compareTo(function.apply(o2));
                if (tagCompare == 0) {
                    return tableComparator.compare(o1, o2);
                }
                return tagCompare;
            };
            table.setItems(filters.sorted(comparator));
            return true;
        });

    }
}
