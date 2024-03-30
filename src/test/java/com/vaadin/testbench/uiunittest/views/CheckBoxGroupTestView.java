package com.vaadin.testbench.uiunittest.views;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

public class CheckBoxGroupTestView extends TestView {
    public static final String NAME = "checkboxgroup";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        CheckBoxGroup<String> group = new CheckBoxGroup<>("Select");
        List<String> items = Arrays.asList("One", "Two", "Three");

        group.addValueChangeListener(e -> {
            if (e.isUserOriginated()) {
                Notification.show(
                        e.getValue().stream().collect(Collectors.joining(",")));
            }
        });
        return group;
    }

}
