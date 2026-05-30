/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.spring;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

@SpringComponent
@ViewScope
public class TestPresenter {

    private final ApplicationScopedMockService service;

    public TestPresenter(ApplicationScopedMockService service) {
        this.service = service;
    }

    public String getMessage() {
        return service.nextMessage();
    }

    public ApplicationScopedMockService getService() {
        return service;
    }
}
