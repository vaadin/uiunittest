package com.vaadin.testbench.uiunittest.views;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class TabSheetTestView extends TestView {
    public static final String NAME = "tabsheet";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.addTab(createSheet("Tab1"), "Tab1");
        tabSheet.addTab(createSheet("Tab2"), "Tab2");
        tabSheet.addSelectedTabChangeListener(e -> {
            if (e.isUserOriginated()) {
                Notification.show("Changed");
            }
        });

        return tabSheet;
    }

    private VerticalLayout createSheet(String content) {
        VerticalLayout layout = new VerticalLayout();
        Label label = new Label(content);
        label.setId("content");
        layout.addComponent(label);
        return layout;
    }
}
