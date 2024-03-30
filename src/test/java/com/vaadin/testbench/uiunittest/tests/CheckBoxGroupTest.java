/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.CheckBoxGroupTestView;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Notification;

public class CheckBoxGroupTest extends UIUnitTest {

    private TestUI ui;
    private CheckBoxGroupTestView view;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        view = navigate(CheckBoxGroupTestView.NAME,
                CheckBoxGroupTestView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void basic() {
        Set<String> value = Set.of("One", "Three");
        test($(CheckBoxGroup.class).first()).setValue(value);
        String message = $(Notification.class).last().getCaption();
        assertTrue(message.contains("One"));
        assertTrue(message.contains("Three"));
        assertFalse(message.contains("Two"));
    }
}
