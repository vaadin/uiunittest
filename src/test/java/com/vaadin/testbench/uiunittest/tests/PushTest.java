/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.PushTestView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

public class PushTest extends UIUnitTest {

    private TestUI ui;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        navigate(PushTestView.NAME, PushTestView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void waitForPush() {
        assertEquals(PushMode.AUTOMATIC,
                ui.getPushConfiguration().getPushMode());
        Label label = $(Label.class).id("push-label");
        assertEquals("", label.getValue());
        Button button = $(Button.class).id("spin-button");
        test(button).click();
        assertFalse(button.isEnabled());
        waitWhile(label,
                l -> l.getStyleName().contains(ValoTheme.LABEL_SPINNER), 10);
        assertEquals("Hello", label.getValue());
        assertTrue(button.isEnabled());
        assertFalse(label.getStyleName().contains(ValoTheme.LABEL_SPINNER));
    }
}
