/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.views;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class ComboBoxTestView extends TestView {
    public static final String NAME = "combobox";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        VerticalLayout layout = new VerticalLayout();
        ComboBox<String> comboBox = new ComboBox<>("Select");
        List<String> items = Arrays.asList("One", "Two", "Three");

        comboBox.addValueChangeListener(e -> {
            if (e.isUserOriginated()) {
                Notification.show(e.getValue());
            }
        });

        comboBox.setNewItemProvider(newValue -> {
            Label label = new Label("New");
            label.setId("new");
            layout.addComponent(label);
            return Optional.of(newValue);
        });

        comboBox.setItems(items);
        layout.addComponent(comboBox);
        return layout;
    }

}
