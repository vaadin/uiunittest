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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

public class FocusTestView extends TestView {
    public static final String NAME = "focus";
    public int blurCount = 0;
    public int focusCount = 0;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        FormLayout layout = new FormLayout();
        TextField firstName = new TextField("First name");
        TextField lastName = new TextField("Last name");
        firstName.focus();
        firstName.addBlurListener(e -> {
            blurCount++;
            Label label = new Label("First name blurred");
            label.setId("blur" + blurCount);
            addComponent(label);
        });
        lastName.addFocusListener(e -> {
            focusCount++;
            Label label = new Label("Last name focused");
            label.setId("focus" + focusCount);
            addComponent(label);
        });
        layout.addComponents(firstName, lastName);
        return layout;
    }

}
