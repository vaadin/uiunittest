/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.testers;

import com.vaadin.data.provider.HierarchicalDataCommunicator;
import com.vaadin.event.CollapseEvent;
import com.vaadin.event.ExpandEvent;
import com.vaadin.ui.TreeGrid;

public class TreeGridTester<T> extends GridTester<T> {

    public TreeGridTester(TreeGrid<T> grid) {
        super(grid);
    }

    /**
     * Simulate clicking of the TreeGrid expand/collapse toggle in hierarchy
     * column on the given item. Resulting expand or collapse event will have
     * isUserOrginated = true;
     * 
     * @param item
     *            The item
     */
    public void clickToggle(T item) {
        assert (isInteractable()) : "Can't interact with disabled or invisible TreeGrid";
        if (getComponent().isExpanded(item)) {
            assert (isCollapseAllowed(item)) : "TreeGrid Collapse not allowed";
            collapse(item);
        } else {
            expand(item);
        }
    }

    private void expand(T item) {
        assert (getComponent().isEnabled()) : "TreeGrid is in disabled state";
        HierarchicalDataCommunicator<T> communicator = getComponent()
                .getDataCommunicator();
        if (!communicator.isExpanded(item) && communicator.hasChildren(item)) {
            communicator.expand(item);
            fireExpandEvent(item);
        }
    }

    private void fireExpandEvent(T item) {
        fireSimulatedEvent(new ExpandEvent<>(getComponent(), item, true));
    }

    private void collapse(T item) {
        assert (getComponent().isEnabled()) : "TreeGrid is in disabled state";
        assert (isCollapseAllowed(item)) : "TreeGrid Collapse not allowed";
        HierarchicalDataCommunicator<T> communicator = getComponent()
                .getDataCommunicator();
        if (communicator.isExpanded(item)) {
            communicator.collapse(item);
            fireCollapseEvent(item);
        }
    }

    private boolean isCollapseAllowed(T item) {
        if (getComponent().getItemCollapseAllowedProvider() == null) {
            return true;
        }
        return getComponent().getItemCollapseAllowedProvider().test(item);
    }

    private void fireCollapseEvent(T item) {
        fireSimulatedEvent(new CollapseEvent<>(getComponent(), item, true));
    }

    protected TreeGrid<T> getComponent() {
        return (TreeGrid<T>) super.getComponent();
    }
}
