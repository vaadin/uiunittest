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
import com.vaadin.testbench.uiunittest.views.TreeGridTestView;
import com.vaadin.testbench.uiunittest.views.TreeGridTestView.Department;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TreeGrid;

public class TreeGridTest extends UIUnitTest {

    private TestUI ui;
    private TreeGridTestView view;

    @Before
    public void setup() throws ServiceException {
        ui = new TestUI();
        mockVaadin(ui);
        view = navigate(TreeGridTestView.NAME, TreeGridTestView.class);
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void expandCollapse() {
        TreeGrid<Department> grid = $(TreeGrid.class).first();

        Department dept = view.getData().getRootDepartments().get(0);

        test(grid).clickToggle(dept);
        assertEquals("Expanded Product Development",
                $(Notification.class).last().getCaption());

        test(grid).clickToggle(dept);
        assertEquals("Collapsed Product Development",
                $(Notification.class).last().getCaption());
    }

    @Test
    public void gridDisabled() {
        @SuppressWarnings("unchecked")
        TreeGrid<Department> grid = $(TreeGrid.class).first();

        test($(Button.class).caption("Disable").first()).click();

        Department dept = view.getData().getRootDepartments().get(0);

        int err = 0;
        try {
            test(grid).clickToggle(dept);
        } catch (AssertionError e) {
            err++;
        }
        assertEquals(1, err);
    }
}