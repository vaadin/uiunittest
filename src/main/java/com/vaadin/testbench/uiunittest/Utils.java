package com.vaadin.testbench.uiunittest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

public class Utils {

    private Utils() {
        // Utility class
    }

    public static <T> Set<T> setOfItems(
            @SuppressWarnings("unchecked") T... items) {
        Set<T> itemSet = new HashSet<>();
        itemSet.addAll(Arrays.asList(items));
        return itemSet;
    }

    public static <T> List<T> listOfItems(
            @SuppressWarnings("unchecked") T... items) {
        List<T> itemList = new ArrayList<>();
        itemList.addAll(Arrays.asList(items));
        return itemList;
    }

    /**
     * Print the component tree for debugging. This includes all components
     * within the UI and all windows. Also notifications are printed.
     * Indentation is used to indicate component hierarchy.
     */
    public static void printComponentTree() {
        // Print component tree for debugging by traversing all
        // components recursively
        System.out.println("Component tree:");
        printComponentTree(UI.getCurrent());
        UI.getCurrent().getWindows().forEach(window -> {
            System.out.println("Window: " + window.getClass()
                    + (window.getId() != null ? " id='" + window.getId() + "'"
                            : "")
                    + (window.getCaption() != null
                            ? " caption='" + window.getCaption() + "'"
                            : "")
                    + (window.getStyleName() != null
                            ? " stylename='" + window.getStyleName() + "'"
                            : "")
                    + (window.isVisible() ? " visible" : " invisible")
                    + (window.isEnabled() ? " enabled" : " disabled"));
            printComponentTree(window);
        });
        // Print notifications
        List<Notification> notifications = UI.getCurrent().getExtensions()
                .stream()
                .filter(ext -> ext.getClass().equals(Notification.class))
                .map(ext -> (Notification) ext).collect(Collectors.toList());
        if (!notifications.isEmpty()) {
            System.out.println("Notifications:");
            notifications.forEach(notification -> {
                System.out.println("  " + notification.getClass()
                        + (notification.getCaption() != null
                                ? " caption='" + notification.getCaption() + "'"
                                : "")
                        + (notification.getDescription() != null
                                ? " description='"
                                        + notification.getDescription() + "'"
                                : ""));
            });
        }
    }

    private static void printComponentTree(HasComponents container) {
        Iterator<Component> iter = container.iterator();
        while (iter.hasNext()) {
            Component component = iter.next();
            StringBuilder builder = new StringBuilder();
            Component parent = component.getParent();
            while (parent != null) {
                builder.append("  ");
                parent = parent.getParent();
            }
            System.out.println(builder.toString() + component.getClass()
                    + (component.getId() != null
                            ? " id='" + component.getId() + "'"
                            : "")
                    + (component.getCaption() != null
                            ? " caption='" + component.getCaption() + "'"
                            : "")
                    + (component.getStyleName() != null
                            ? " stylename='" + component.getStyleName() + "'"
                            : "")
                    + (component.isVisible() ? " visible" : " invisible")
                    + (component.isEnabled() ? " enabled" : " disabled"));
            if (component instanceof HasComponents) {
                printComponentTree((HasComponents) component);
            }
        }
    }

}
