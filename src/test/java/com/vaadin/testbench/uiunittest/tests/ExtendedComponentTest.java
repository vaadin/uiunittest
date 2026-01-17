package com.vaadin.testbench.uiunittest.tests;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.ExtendedComponentView;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class ExtendedComponentTest extends UIUnitTest {

    private TestUI ui;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        navigate(ExtendedComponentView.NAME, ExtendedComponentView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void extendedComponentCanBeFoundAsBaseClass() {
        Label label = $(Label.class).id("my-label");
        assertEquals("Value: none", label.getValue());
        test($(TextField.class).id("my-field")).setValue("Test Value");
        assertEquals("Value: Test Value", label.getValue());
    }
}
