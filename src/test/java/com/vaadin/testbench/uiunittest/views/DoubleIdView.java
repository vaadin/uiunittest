package com.vaadin.testbench.uiunittest.views;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class DoubleIdView extends TestView {
    public static final String NAME = "double-id";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        VerticalLayout layout = new VerticalLayout();
        Button button1 = new Button("Button 1");
        button1.setId("same");
        Button button2 = new Button("Button 2");
        button2.setId("same");
        layout.addComponents(button1, button2);
        return layout;
    }

}
