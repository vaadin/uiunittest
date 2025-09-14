package com.vaadin.testbench.uiunittest.views;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class WindowView extends TestView {
    public static final String NAME = "window";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        Window window = new Window("Test Window");
        window.setId("test-window");
        window.setWidth("400px");
        window.setHeight("300px");
        VerticalLayout layout = new VerticalLayout();
        Label label = new Label("This is a test window.");
        label.setId("window-label");
        layout.addComponent(label);
        window.setContent(layout);
        UI.getCurrent().addWindow(window);
        return new Label("Window opened");
    }

}
