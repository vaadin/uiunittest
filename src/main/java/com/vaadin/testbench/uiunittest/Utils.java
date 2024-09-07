package com.vaadin.testbench.uiunittest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {

    public static <T> Set<T> setOfItems(
            @SuppressWarnings("unchecked") T... items) {
        Set<T> itemSet = new HashSet<>();
        for (T item : items) {
            itemSet.add(item);
        }
        return itemSet;
    }

    public static <T> List<T> listOfItems(
            @SuppressWarnings("unchecked") T... items) {
        List<T> itemList = new ArrayList<>();
        for (T item : items) {
            itemList.add(item);
        }
        return itemList;
    }
}
