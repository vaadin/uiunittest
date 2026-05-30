/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.spring;

import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;

@SpringComponent
@Scope("prototype")
public class AutowiredTestButton extends Button {

    public AutowiredTestButton() {
        super("Autowired action");
        setId("autowired-button");
    }
}
