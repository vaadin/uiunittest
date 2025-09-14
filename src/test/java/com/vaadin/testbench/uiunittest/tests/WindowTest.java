package com.vaadin.testbench.uiunittest.tests;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.WindowView;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class WindowTest extends UIUnitTest {

    private TestUI ui;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        navigate(WindowView.NAME, WindowView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void labelInWindow() {
        Window window = $(Window.class).first();
        assertEquals("Test Window", window.getCaption());
        Label label = $(Label.class).id("window-label");
        assertEquals("This is a test window.", label.getValue());
    }
}
