package com.vaadin.testbench.uiunittest.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.GridTestView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;

public class DefaultTest extends UIUnitTest {

    private TestUI ui;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void defaultTest() {
        assertEquals("Menu", $(Label.class).id("menu").getValue());
        assertTrue($(Grid.class).first() == null);
        test($(Button.class).caption(GridTestView.NAME).first()).click();
        assertTrue($(Grid.class).first() != null);
    }
}
