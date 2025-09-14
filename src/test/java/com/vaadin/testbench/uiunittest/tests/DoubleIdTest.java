package com.vaadin.testbench.uiunittest.tests;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.DoubleIdView;

public class DoubleIdTest extends UIUnitTest {

    private TestUI ui;
    private DoubleIdView view;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        view = navigate(DoubleIdView.NAME, DoubleIdView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void searchByIdThowsAssertionError() {
        boolean thrown = false;
        try {
            test($(com.vaadin.ui.Button.class).id("same"));
        } catch (AssertionError e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

}
