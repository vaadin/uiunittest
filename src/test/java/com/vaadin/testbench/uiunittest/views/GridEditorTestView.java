/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.views;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.data.Binder;
import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class GridEditorTestView extends TestView {
    public static final String NAME = "grid-editor";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        VerticalLayout layout = new VerticalLayout();

        TextField nameField = new TextField();
        nameField.setId("name");
        TextField valueField = new TextField();
        valueField.setId("value");

        Binder<Data> binder = new Binder<>();

        Grid<Data> grid = new Grid<>();
        grid.getEditor().setBinder(binder);
        grid.getEditor().setBuffered(true);
        grid.getEditor().setEnabled(true);
        grid.addColumn(data -> data.getName()).setCaption("Name")
                .setEditorComponent(nameField,
                        (data, value) -> data.setName(value))
                .setEditable(true);
        grid.addColumn(data -> data.getValue()).setCaption("Value")
                .setEditorComponent(valueField,
                        (data, value) -> data.setValue(value))
                .setEditable(true);
        grid.addItemClickListener(e -> {
            grid.getEditor().editRow(e.getRowIndex());
        });
        grid.getEditor().addOpenListener(e -> {
            Notification.show("Editing: " + e.getBean().getName());
        });
        grid.getEditor().addSaveListener(e -> {
            Notification.show("Saved: " + e.getBean().getName());
        });
        grid.getEditor().addCancelListener(e -> {
            Notification.show("Cancelled: " + e.getBean().getName());
        });

        List<Data> items = IntStream.range(0, 99)
                .mapToObj(i -> new Data("Data" + i, "Value" + i))
                .collect(Collectors.toList());
        grid.setItems(items);

        Button disable = new Button("Disable");
        disable.addClickListener(e -> {
            grid.setEnabled(false);
        });

        Button unbuffered = new Button("Unbuffered");
        unbuffered.addClickListener(e -> {
            grid.getEditor().setBuffered(false);
        });

        layout.addComponents(grid, disable, unbuffered);
        layout.setExpandRatio(grid, 1);
        return layout;
    }

    public static class Data {
        private String name;
        private String value;

        public Data(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
