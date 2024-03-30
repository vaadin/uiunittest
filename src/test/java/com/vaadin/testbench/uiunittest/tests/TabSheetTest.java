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
import com.vaadin.testbench.uiunittest.views.TabSheetTestView;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

public class TabSheetTest extends UIUnitTest {

    private TestUI ui;
    private TabSheetTestView view;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        view = navigate(TabSheetTestView.NAME, TabSheetTestView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void basic() {
        TabSheet tabSheet = $(TabSheet.class).single();
        Tab tab = test(tabSheet).tab("Tab2");
        test(tabSheet).click(tab);
        assertEquals("Changed", $(Notification.class).last().getCaption());
        VerticalLayout layout = (VerticalLayout) test(tabSheet).current();
        assertEquals("Tab2", $(layout, Label.class).first().getValue());
    }
}
