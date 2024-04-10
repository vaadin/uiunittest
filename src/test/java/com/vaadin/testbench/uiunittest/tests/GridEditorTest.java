package com.vaadin.testbench.uiunittest.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.GridEditorTestView;
import com.vaadin.testbench.uiunittest.views.GridEditorTestView.Data;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

public class GridEditorTest extends UIUnitTest {

    private TestUI ui;
    private GridEditorTestView view;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        view = navigate(GridEditorTestView.NAME, GridEditorTestView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void editItem_disabled() {
        Grid<Data> grid = $(Grid.class).single();

        $(Button.class).caption("Disable").single().click();

        int err = 0;
        try {
            test(grid).edit(0);
        } catch (AssertionError e) {
            err++;
        }
        assertEquals(1, err);
    }

    @Test
    public void closed_save() {
        Grid<Data> grid = $(Grid.class).single();

        int err = 0;
        try {
            test(grid).save();
        } catch (AssertionError e) {
            err++;
        }
        assertEquals(1, err);
    }

    @Test
    public void closed_cancel() {
        Grid<Data> grid = $(Grid.class).single();

        int err = 0;
        try {
            test(grid).cancel();
        } catch (AssertionError e) {
            err++;
        }
        assertEquals(1, err);
    }

    @Test
    public void editItem_buffered() {
        Grid<Data> grid = $(Grid.class).single();

        assertEquals("Data0", test(grid).cell(0, 0));
        assertEquals("Value0", test(grid).cell(1, 0));

        test(grid).edit(0);
        assertEquals("Editing: Data0",
                $(Notification.class).last().getCaption());
        assertTrue(test(grid).editorOpen());

        test($(TextField.class).id("name")).setValue("New name");
        test($(TextField.class).id("value")).setValue("New value");
        test(grid).save();
        assertEquals("Saved: New name",
                $(Notification.class).last().getCaption());
        assertFalse(test(grid).editorOpen());

        assertEquals("New name", test(grid).cell(0, 0));
        assertEquals("New value", test(grid).cell(1, 0));
    }

    @Test
    public void editItem_cancel() {
        Grid<Data> grid = $(Grid.class).single();

        assertEquals("Data0", test(grid).cell(0, 0));
        assertEquals("Value0", test(grid).cell(1, 0));

        test(grid).edit(0);
        assertEquals("Editing: Data0",
                $(Notification.class).last().getCaption());
        assertTrue(test(grid).editorOpen());

        test($(TextField.class).id("name")).setValue("New name");
        test($(TextField.class).id("value")).setValue("New value");
        test(grid).cancel();
        assertEquals("Cancelled: Data0",
                $(Notification.class).last().getCaption());
        assertFalse(test(grid).editorOpen());

        assertEquals("Data0", test(grid).cell(0, 0));
        assertEquals("Value0", test(grid).cell(1, 0));
    }

    @Test
    public void editItem_unbuffered() {
        Grid<Data> grid = $(Grid.class).single();

        $(Button.class).caption("Unbuffered").single().click();

        assertEquals("Data0", test(grid).cell(0, 0));
        assertEquals("Value0", test(grid).cell(1, 0));

        test(grid).edit(0);
        assertEquals("Editing: Data0",
                $(Notification.class).last().getCaption());
        assertTrue(test(grid).editorOpen());

        test($(TextField.class).id("name")).setValue("New name");
        test($(TextField.class).id("value")).setValue("New value");
        test(grid).save();
        assertEquals("Saved: New name",
                $(Notification.class).last().getCaption());
        assertFalse(test(grid).editorOpen());

        assertEquals("New name", test(grid).cell(0, 0));
        assertEquals("New value", test(grid).cell(1, 0));
    }

}
