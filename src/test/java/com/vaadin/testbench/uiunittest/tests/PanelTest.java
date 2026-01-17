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
import com.vaadin.testbench.uiunittest.views.PanelTestView;
import com.vaadin.ui.Label;

public class PanelTest extends UIUnitTest {

    private TestUI ui;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        navigate(PanelTestView.NAME, PanelTestView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void findInPanel() {
        assertEquals("First", $(Label.class).id("first").getValue());

        assertEquals("Second",
                $(Label.class).styleName("second").single().getValue());

        assertEquals("Third",
                $(Label.class).caption("Third").single().getValue());
    }
}
