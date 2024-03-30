package com.vaadin.testbench.uiunittest.views;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

public class ComboBoxTestView extends TestView {
    public static final String NAME = "combobox";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        ComboBox<String> comboBox = new ComboBox<>("Select");
        List<String> items = Arrays.asList("One", "Two", "Three");

        comboBox.addValueChangeListener(e -> {
            if (e.isUserOriginated()) {
                Notification.show(e.getValue());
            }
        });

        comboBox.setNewItemProvider(newValue -> {
            return Optional.of(newValue);
        });

        comboBox.setItems(items);
        return comboBox;
    }

}
