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

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;

public class GridTestView extends TestView {

    public static final String NAME = "grid";
    private Bean bean = new Bean(0, "Value 0");

    @Override
    public String getName() {
        return NAME;
    }

    protected List<Bean> data;

    @Override
    public Component getComponent() {
        VerticalLayout content = new VerticalLayout();
        data = IntStream.range(0, 10).mapToObj(i -> new Bean(i, "Value " + i))
                .collect(Collectors.toList());

        Grid<Bean> grid = new Grid<>();
        grid.addColumn(item -> item.getId()).setCaption("ID").setSortable(true)
                .setComparator((a, b) -> a.getId() - b.getId());
        grid.addComponentColumn(item -> {
            HorizontalLayout layout = new HorizontalLayout();
            TextField field = new TextField();
            field.setValue(item.getValue());
            field.addFocusListener(e -> Notification.show("TextField focused"));
            field.addBlurListener(e -> Notification.show("TextField blurred"));
            Button button = new Button("Save");
            button.addClickListener(e -> {
                item.setValue(field.getValue());
                grid.getDataProvider().refreshItem(item);
            });
            layout.addComponents(field, button);
            return layout;
        }).setCaption("VALUE").setHidable(true).setSortable(false);

        grid.setItems(data);
        grid.setSelectionMode(SelectionMode.NONE);

        grid.setStyleGenerator(item -> item.getValue() + "_" + item.getId());
        grid.setDescriptionGenerator(
                item -> item.getId() + ":" + item.getValue());

        RadioButtonGroup<SelectionMode> group = new RadioButtonGroup<>("Mode");
        group.setItems(SelectionMode.values());

        group.addValueChangeListener(e -> {
            if (e.isUserOriginated()) {
                grid.setSelectionMode(e.getValue());
                if (e.getValue() != SelectionMode.NONE) {
                    grid.addSelectionListener(event -> {
                        if (event.isUserOriginated()) {
                            event.getFirstSelectedItem().ifPresent(item -> {
                                Notification.show("Select " + item.getId());
                            });
                        }
                    });
                }
            }
        });

        grid.addSortListener(e -> {
            if (e.isUserOriginated()) {
                Notification.show(
                        "Sort: " + e.getSortOrder().get(0).getDirection());
            }
        });

        grid.addColumnVisibilityChangeListener(e -> {
            if (e.isUserOriginated()) {
                Notification.show("Hidden: " + e.isHidden());
            }
        });

        Button button = new Button("Disable");
        button.addClickListener(e -> {
            grid.setEnabled(false);
        });

        Label clicked = new Label("");
        clicked.setId("clicked");

        grid.addItemClickListener(e -> {
            clicked.setValue(e.getItem().getValue());
        });

        content.addComponents(grid, group, button, clicked);
        content.setExpandRatio(grid, 1);
        return content;
    }

    @Override
    public List<Bean> getData() {
        return data;
    }

    public static class Bean {
        private int id;
        private String value;

        public Bean(int id, String value) {
            this.setId(id);
            this.setValue(value);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
