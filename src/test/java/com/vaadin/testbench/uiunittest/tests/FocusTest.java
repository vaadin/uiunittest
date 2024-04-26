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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.FocusTestView;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class FocusTest extends UIUnitTest {

    private TestUI ui;
    private FocusTestView view;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        view = navigate(FocusTestView.NAME, FocusTestView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void focusByValue() {
        test($(TextField.class).caption("Last name").first())
                .setValue("New value");

        assertEquals("Last name focused",
                $(Label.class).id("focus1").getValue());
        assertEquals("First name blurred",
                $(Label.class).id("blur1").getValue());

        test($(TextField.class).caption("Last name").first())
                .setValue("Another value");
        assertEquals(1, view.focusCount);
        assertEquals(1, view.blurCount);
    }

    @Test
    public void focus() {
        test($(TextField.class).caption("Last name").first()).focus();
        assertEquals("Last name focused",
                $(Label.class).id("focus1").getValue());
        assertEquals("First name blurred",
                $(Label.class).id("blur1").getValue());

        test($(TextField.class).caption("Last name").first()).focus();
        assertEquals(1, view.focusCount);
        assertEquals(1, view.blurCount);
    }
}
