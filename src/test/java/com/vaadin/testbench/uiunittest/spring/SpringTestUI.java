/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.spring;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Push
@SpringUI
public class SpringTestUI extends UI {

    private final SpringNavigator springNavigator;
    private final ApplicationScopedMockService service;

    @Autowired
    public SpringTestUI(SpringNavigator springNavigator,
            ApplicationScopedMockService service) {
        this.springNavigator = springNavigator;
        this.service = service;
    }

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        setContent(content);
        springNavigator.init(this, content);
    }

    public ApplicationScopedMockService getService() {
        return service;
    }
}
