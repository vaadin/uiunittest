package com.vaadin.testbench.uiunittest;

import com.vaadin.navigator.View;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public abstract class TestView extends VerticalLayout implements View {

    public TestView() {
        Label title = new Label(getName());
        Component component = getComponent();
        addComponents(title, component);
        setExpandRatio(component, 1);
    }

    public abstract String getName();

    public abstract Component getComponent();
}
