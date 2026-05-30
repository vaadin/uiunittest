/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.spring;

import java.util.concurrent.atomic.AtomicInteger;

import com.vaadin.spring.annotation.SpringComponent;

@SpringComponent
public class ApplicationScopedMockService {

    private final AtomicInteger invocationCount = new AtomicInteger();

    public String nextMessage() {
        return "service-message-" + invocationCount.incrementAndGet();
    }
}
