package com.vaadin.testbench.uiunittest.views;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;

public class GridTestView extends TestView {

    public static final String NAME = "grid";

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
        grid.addColumn(item -> item.getId()).setCaption("ID");
        grid.addComponentColumn(item -> {
            HorizontalLayout layout = new HorizontalLayout();
            TextField field = new TextField();
            field.setValue(item.getValue());
            Button button = new Button("Save");
            button.addClickListener(e -> {
                item.setValue(field.getValue());
                grid.getDataProvider().refreshItem(item);
            });
            layout.addComponents(field, button);
            return layout;
        }).setCaption("VALUE");

        grid.setItems(data);
        grid.setSelectionMode(SelectionMode.NONE);

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

        Button button = new Button("Disable");
        button.addClickListener(e -> {
            grid.setEnabled(false);
        });

        content.addComponents(grid, group, button);
        content.setExpandRatio(grid, 1);
        return content;
    }

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
