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
import com.vaadin.testbench.uiunittest.views.ComboBoxTestView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

public class ComboBoxTest extends UIUnitTest {

    private TestUI ui;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        navigate(ComboBoxTestView.NAME, ComboBoxTestView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void basic() {
        test($(ComboBox.class).first()).clickItem("Two");
        assertEquals("Two", $(Notification.class).last().getCaption());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void filter() {
        test($(ComboBox.class).first()).setInput("Three");
        assertEquals("Three", $(Notification.class).last().getCaption());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void newValue() {
        test($(ComboBox.class).first()).setInput("Four");
        assertEquals("Four", $(Notification.class).last().getCaption());
        assertEquals("New", $(Label.class).id("new").getValue());
    }

}
