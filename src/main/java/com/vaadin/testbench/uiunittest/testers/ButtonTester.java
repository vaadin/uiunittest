/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.testers;

import com.vaadin.ui.Button;

public class ButtonTester extends Tester<Button> {

    public ButtonTester(Button component) {
        super(component);
    }

    /**
     * Assert that Button is enabled and visible and produce simulated
     * ClickEvent if it is. Button will gain focus and fire focus event as a
     * side effect.
     */
    public void click() {
        assert (isInteractable()) : "Button is not enabled or visible";
        focus();
        getComponent().click();
        if (getComponent().isDisableOnClick()) {
            getComponent().setEnabled(false);
        }
    }
}
