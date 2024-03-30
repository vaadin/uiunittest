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

import java.util.Collections;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.GridTestView;
import com.vaadin.testbench.uiunittest.views.GridTestView.Bean;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
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

    @Test
    public void multiSelection() {
        Set<Bean> set = Set.of(view.getData().get(0), view.getData().get(1));

        Grid<Bean> grid = $(Grid.class).single();
        test($(RadioButtonGroup.class).caption("Mode").first())
                .setValue(SelectionMode.MULTI);
        test(grid).click(0, 0);
        assertEquals("Select 0", $(Notification.class).last().getCaption());
        test(grid).click(0, 1);
        assertEquals("Select 0", $(Notification.class).last().getCaption());
        assertEquals(2, grid.getSelectedItems().size());
        assertEquals(set, grid.getSelectedItems());

        set = Set.of(view.getData().get(0));
        test(grid).click(0, 1);
        assertEquals(set, grid.getSelectedItems());
    }

    @Test
    public void singleSelection() {
        Set<Bean> set = Set.of(view.getData().get(1));

        Grid<Bean> grid = $(Grid.class).single();
        test($(RadioButtonGroup.class).caption("Mode").first())
                .setValue(SelectionMode.SINGLE);
        test(grid).click(0, 0);
        assertEquals("Select 0", $(Notification.class).last().getCaption());
        test(grid).click(0, 1);
        assertEquals("Select 1", $(Notification.class).last().getCaption());
        assertEquals(1, grid.getSelectedItems().size());
        assertEquals(set, grid.getSelectedItems());

        test(grid).click(0, 1);
        assertEquals(Collections.emptySet(), grid.getSelectedItems());
    }

    @Test
    public void gridComponents() {
        Grid<Bean> grid = $(Grid.class).single();
        test($(RadioButtonGroup.class).caption("Mode").first())
                .setValue(SelectionMode.SINGLE);

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

    @Test
    public void gridValues() {
        Grid<Bean> grid = $(Grid.class).single();
        test($(RadioButtonGroup.class).caption("Mode").first())
                .setValue(SelectionMode.SINGLE);
        for (int i = 0; i < 10; i++) {
            assertEquals(i, test(grid).cell(0, i));
        }
    }

    @Test
    public void gridDisabled() {
        Grid<Bean> grid = $(Grid.class).single();
        test($(RadioButtonGroup.class).caption("Mode").first())
                .setValue(SelectionMode.SINGLE);

        test($(Button.class).caption("Disable").first()).click();
        int err = 0;
        try {
            test(grid).click(0, 0);
        } catch (AssertionError e) {
            err++;
        }
        assertEquals(1, err);
    }
}
