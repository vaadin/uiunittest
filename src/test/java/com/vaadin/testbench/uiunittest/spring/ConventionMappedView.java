/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.spring;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;

@SpringView
public class ConventionMappedView extends Label implements View {

    public ConventionMappedView() {
        super("Convention mapped");
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }
}
