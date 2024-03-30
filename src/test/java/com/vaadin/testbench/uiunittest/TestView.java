/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
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
