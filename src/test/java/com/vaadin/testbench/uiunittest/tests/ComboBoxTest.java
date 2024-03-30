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
import com.vaadin.ui.Notification;

public class ComboBoxTest extends UIUnitTest {

    private TestUI ui;
    private ComboBoxTestView view;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        view = navigate(ComboBoxTestView.NAME, ComboBoxTestView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void basic() {
        test($(ComboBox.class).first()).setValue("Two");
        assertEquals("Two", $(Notification.class).last().getCaption());
    }

    @Test
    public void filter() {
        test($(ComboBox.class).first()).setFilter("Three");
        assertEquals("Three", $(Notification.class).last().getCaption());
    }

    @Test
    public void newValue() {
        test($(ComboBox.class).first()).setFilter("Four");
        assertEquals("Four", $(Notification.class).last().getCaption());
    }

}
