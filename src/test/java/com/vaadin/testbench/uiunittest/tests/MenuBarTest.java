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
import com.vaadin.testbench.uiunittest.views.MenuBarTestView;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;

public class MenuBarTest extends UIUnitTest {

    private TestUI ui;
    private MenuBarTestView view;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        view = navigate(MenuBarTestView.NAME, MenuBarTestView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void menuBar() {
        MenuBar bar = $(MenuBar.class).first();

        MenuItem menu1 = test(bar).item("Menu 1");
        test(bar).click(menu1);
        assertEquals("Menu 1", $(Notification.class).last().getCaption());

        MenuItem subMenu1 = test(bar).item("SubMenu 1");
        test(bar).click(subMenu1);
        assertEquals("SubMenu 1", $(Notification.class).last().getCaption());

        MenuItem subSubMenu1 = test(bar).item("SubSubMenu 1");
        test(bar).click(subSubMenu1);
        assertEquals("SubSubMenu 1", $(Notification.class).last().getCaption());

        MenuItem menu2 = test(bar).item(3);
        int err = 0;
        try {
            test(bar).click(menu2);
        } catch (AssertionError e) {
            err++;
        }
        assertEquals(1, err);
    }
}
