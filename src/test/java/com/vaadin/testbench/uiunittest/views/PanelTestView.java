/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.views;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class PanelTestView extends TestView {
    public static final String NAME = "panel";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        Panel panel = new Panel();
        VerticalLayout layout = new VerticalLayout();
        Label first = new Label("First");
        first.setId("first");
        Label second = new Label("Second");
        second.addStyleName("second");
        Label third = new Label("Third");
        third.setCaption("Third");
        layout.addComponents(first, second, third);
        panel.setContent(layout);
        return panel;
    }

}
