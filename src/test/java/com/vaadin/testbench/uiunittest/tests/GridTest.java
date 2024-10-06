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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.Utils;
import com.vaadin.testbench.uiunittest.views.GridTestView;
import com.vaadin.testbench.uiunittest.views.GridTestView.Bean;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;

public class GridTest extends UIUnitTest {

    private TestUI ui;
    private GridTestView view;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        view = navigate(GridTestView.NAME, GridTestView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void multiSelection() {
        Set<Bean> set = Utils.setOfItems(view.getData().get(0),
                view.getData().get(1));

        Grid<Bean> grid = $(Grid.class).single();
        assertFalse(test(grid).isFocused());
        test($(RadioButtonGroup.class).caption("Mode").first())
                .clickItem(SelectionMode.MULTI);
        test(grid).clickToSelect(0);
        assertTrue(test(grid).isFocused());
        assertEquals("Select 0", $(Notification.class).last().getCaption());
        test(grid).clickToSelect(1);
        assertEquals("Select 0", $(Notification.class).last().getCaption());
        assertEquals(2, grid.getSelectedItems().size());
        assertEquals(set, grid.getSelectedItems());

        set = Utils.setOfItems(view.getData().get(0));
        test(grid).clickToSelect(1);
        assertEquals(set, grid.getSelectedItems());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void singleSelection() {
        Set<Bean> set = Utils.setOfItems(view.getData().get(1));

        Grid<Bean> grid = $(Grid.class).single();
        assertFalse(test(grid).isFocused());
        test($(RadioButtonGroup.class).caption("Mode").first())
                .clickItem(SelectionMode.SINGLE);
        test(grid).click(0, 0);
        assertTrue(test(grid).isFocused());
        assertEquals("Select 0", $(Notification.class).last().getCaption());
        test(grid).click(0, 1);
        assertEquals("Select 1", $(Notification.class).last().getCaption());
        assertEquals(1, grid.getSelectedItems().size());
        assertEquals(set, grid.getSelectedItems());

        test(grid).click(0, 1);
        assertEquals(Collections.emptySet(), grid.getSelectedItems());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void gridComponents() {
        Grid<Bean> grid = $(Grid.class).single();
        test($(RadioButtonGroup.class).caption("Mode").first())
                .clickItem(SelectionMode.SINGLE);

        for (int i = 0; i < 10; i++) {
            HorizontalLayout layout = (HorizontalLayout) test(grid).cell(1, i);
            assertEquals("Value " + i,
                    $(layout, TextField.class).single().getValue());
            String newValue = "New value " + i;
            test($(layout, TextField.class).single()).setValue(newValue);
            test($(layout, Button.class).single()).click();
            assertEquals(newValue,
                    $(layout, TextField.class).single().getValue());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void gridValues() {
        Grid<Bean> grid = $(Grid.class).single();
        for (int i = 0; i < 10; i++) {
            Bean item = test(grid).item(i);
            assertEquals(i, test(grid).cell(0, i));
            assertEquals(item.getId() + ":" + item.getValue(),
                    test(grid).description(i));
            assertEquals(item.getValue() + "_" + item.getId(),
                    test(grid).styleName(i));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void gridDisabled() {
        Grid<Bean> grid = $(Grid.class).single();
        test($(RadioButtonGroup.class).caption("Mode").first())
                .clickItem(SelectionMode.SINGLE);

        test($(Button.class).caption("Disable").first()).click();
        int err = 0;
        try {
            test(grid).click(0, 0);
        } catch (AssertionError e) {
            err++;
        }
        assertEquals(1, err);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void itemClick() {
        Grid<Bean> grid = $(Grid.class).single();
        assertFalse(test(grid).isFocused());

        assertEquals("", $(Label.class).id("clicked").getValue());

        for (int i = 0; i < 10; i++) {
            test(grid).click(0, i);
            assertTrue(test(grid).isFocused());
            assertEquals("Value " + i, $(Label.class).id("clicked").getValue());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void sorting() {
        Grid<Bean> grid = $(Grid.class).single();

        test(grid).toggleColumnSorting(0);
        assertEquals("Sort: ASCENDING",
                $(Notification.class).last().getCaption());
        for (int i = 0; i < 10; i++) {
            assertEquals(i, test(grid).cell(0, i));
        }

        test(grid).toggleColumnSorting(0);
        assertEquals("Sort: DESCENDING",
                $(Notification.class).last().getCaption());
        for (int i = 0; i < 10; i++) {
            assertEquals(9 - i, test(grid).cell(0, i));
        }

        test(grid).toggleColumnSorting(0);
        assertEquals("Sort: ASCENDING",
                $(Notification.class).last().getCaption());
        for (int i = 0; i < 10; i++) {
            assertEquals(i, test(grid).cell(0, i));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void sortingNotEnabled() {
        Grid<Bean> grid = $(Grid.class).single();

        int err = 0;
        try {
            test(grid).toggleColumnSorting(1);
        } catch (AssertionError e) {
            err++;
        }
        assertEquals(1, err);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void hiding() {
        Grid<Bean> grid = $(Grid.class).single();

        test(grid).toggleColumnVisibility(1);
        assertEquals("Hidden: true", $(Notification.class).last().getCaption());
        assertTrue(grid.getColumns().get(1).isHidden());

        int err = 0;
        try {
            Object value = test(grid).cell(1, 0);
        } catch (AssertionError e) {
            err++;
        }
        assertEquals(1, err);

        test(grid).toggleColumnVisibility("VALUE");
        assertEquals("Hidden: false",
                $(Notification.class).last().getCaption());
        assertFalse(grid.getColumns().get(1).isHidden());

        Object value = test(grid).cell(1, 0);
        assertFalse(value == null);
    }

    @Test
    public void focusBlur() {
        @SuppressWarnings("unchecked")
        Grid<Bean> grid = $(Grid.class).single();
        HorizontalLayout layout = (HorizontalLayout) test(grid).cell(1, 0);
        test($(layout, TextField.class).single()).focus();
        assertEquals("TextField focused",
                $(Notification.class).last().getCaption());
        test($(layout, Button.class).single()).focus();
        assertEquals("TextField blurred",
                $(Notification.class).last().getCaption());
    }
}
